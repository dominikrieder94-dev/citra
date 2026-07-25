// Copyright 2017 Citra Emulator Project
// Licensed under GPLv2 or any later version
// Refer to the license.txt file included.

#include <algorithm>
#include <chrono>
#include "core.h"
#include "core/hw/gpu.h"
#include "core/perf_stats.h"
#include "core/settings.h"

using namespace std::chrono_literals;
using DoubleSecs = std::chrono::duration<double, std::chrono::seconds::period>;
using std::chrono::duration_cast;
using std::chrono::microseconds;

namespace Core {

void PerfStats::BeginSystemFrame() {
    frame_limiter.DoFrameLimiting(System::GetInstance().CoreTiming().GetGlobalTimeUs());
}

void PerfStats::EndSystemFrame() {
    auto frame_end = Clock::now();
    previous_frame_length = frame_end - previous_frame_end;
    previous_frame_end = frame_end;
    system_frames += 1;

    const auto guest_now = System::GetInstance().CoreTiming().GetGlobalTimeUs();
    std::scoped_lock lock{sample_mutex};
    // A long wall gap means pause/resume or another discontinuity; restart the windows so the
    // next estimates don't average across it.
    if (frame_sample_count > 0) {
        const auto& prev = frame_sample_ring[(frame_sample_head + FRAME_SAMPLE_RING_SIZE - 1) %
                                             FRAME_SAMPLE_RING_SIZE];
        if (frame_end - prev.wall > std::chrono::milliseconds(250)) {
            frame_sample_count = 0;
            game_sample_count = 0;
        }
    }
    frame_sample_ring[frame_sample_head] = {frame_end, guest_now};
    frame_sample_head = (frame_sample_head + 1) % FRAME_SAMPLE_RING_SIZE;
    frame_sample_count = std::min(frame_sample_count + 1, FRAME_SAMPLE_RING_SIZE);
}

void PerfStats::EndGameFrame() {
    game_frames += 1;

    std::scoped_lock lock{sample_mutex};
    game_sample_ring[game_sample_head] = Clock::now();
    game_sample_head = (game_sample_head + 1) % FRAME_SAMPLE_RING_SIZE;
    game_sample_count = std::min(game_sample_count + 1, FRAME_SAMPLE_RING_SIZE);
}

PerfStats::Results PerfStats::GetAndResetStats(microseconds current_system_time_us) {
    const auto now = Clock::now();
    // Walltime elapsed since stats were reset
    const auto interval = duration_cast<DoubleSecs>(now - reset_point).count();
    const auto system_us_per_second = (current_system_time_us - reset_point_system_us) / interval;

    Results results{};
    results.system_fps = static_cast<double>(system_frames) / interval;
    results.game_fps = static_cast<double>(game_frames) / interval;
    results.emulation_speed = system_us_per_second.count() / 1'000'000.0;

    // Prefer the frame-boundary-aligned rolling estimates when enough samples exist: they are
    // immune to the intra-frame execution staircase that makes the raw interval figures above
    // wobble by several percent at a perfectly steady emulation speed, while still showing real
    // slowdowns (a slow frame stays inside the ~1.5 s window until it ages out).
    {
        std::scoped_lock lock{sample_mutex};
        constexpr std::size_t MAX_SYSTEM_SPANS = 90; // ~1.5 s of 60 Hz system frames
        if (frame_sample_count >= 16) {
            const std::size_t spans = std::min(frame_sample_count - 1, MAX_SYSTEM_SPANS);
            const auto& newest = frame_sample_ring[(frame_sample_head + FRAME_SAMPLE_RING_SIZE -
                                                    1) %
                                                   FRAME_SAMPLE_RING_SIZE];
            const auto& oldest = frame_sample_ring[(frame_sample_head + 2 * FRAME_SAMPLE_RING_SIZE -
                                                    1 - spans) %
                                                   FRAME_SAMPLE_RING_SIZE];
            const double wall_s = duration_cast<DoubleSecs>(newest.wall - oldest.wall).count();
            if (wall_s > 0.0) {
                results.system_fps = static_cast<double>(spans) / wall_s;
                results.emulation_speed =
                    duration_cast<DoubleSecs>(newest.guest - oldest.guest).count() / wall_s;
            }
        }
        constexpr std::size_t MAX_GAME_SPANS = 45; // ~1.5 s of 30 Hz game frames
        if (game_sample_count >= 8) {
            const std::size_t spans = std::min(game_sample_count - 1, MAX_GAME_SPANS);
            const auto newest = game_sample_ring[(game_sample_head + FRAME_SAMPLE_RING_SIZE - 1) %
                                                 FRAME_SAMPLE_RING_SIZE];
            const auto oldest = game_sample_ring[(game_sample_head + 2 * FRAME_SAMPLE_RING_SIZE -
                                                  1 - spans) %
                                                 FRAME_SAMPLE_RING_SIZE];
            const double wall_s = duration_cast<DoubleSecs>(newest - oldest).count();
            if (wall_s > 0.0) {
                results.game_fps = static_cast<double>(spans) / wall_s;
            }
        }
    }

    // Reset counters
    reset_point = now;
    reset_point_system_us = current_system_time_us;
    system_frames = 0;
    game_frames = 0;

    return results;
}

double PerfStats::GetLastFrameTimeScale() const {
    return duration_cast<DoubleSecs>(previous_frame_length).count() * GPU::SCREEN_REFRESH_RATE;
}

void FrameLimiter::DoFrameLimiting(microseconds current_system_time_us) {
    if (!Settings::values.use_frame_limit) {
        return;
    }

    auto now = Clock::now();
    const double sleep_scale = Settings::values.frame_limit / 100.0;

    // Max lag caused by slow frames. Shouldn't be more than the length of a frame at the current
    // speed percent or it will clamp too much and prevent this from properly limiting to that
    // percent. High values means it'll take longer after a slow frame to recover and start limiting
    const auto max_lag_time_us = duration_cast<microseconds>(DoubleSecs(25ms / sleep_scale));
    frame_limiting_delta_err += duration_cast<microseconds>(
        DoubleSecs((current_system_time_us - previous_system_time_us) / sleep_scale));
    frame_limiting_delta_err -= duration_cast<microseconds>(now - previous_walltime);
    frame_limiting_delta_err =
        std::clamp(frame_limiting_delta_err, -max_lag_time_us, max_lag_time_us);

    if (frame_limiting_delta_err > microseconds::zero()) {
        std::this_thread::sleep_for(frame_limiting_delta_err);
        auto now_after_sleep = Clock::now();
        frame_limiting_delta_err -= duration_cast<microseconds>(now_after_sleep - now);
        now = now_after_sleep;
    }

    previous_system_time_us = current_system_time_us;
    previous_walltime = now;
}

} // namespace Core

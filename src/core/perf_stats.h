// Copyright 2017 Citra Emulator Project
// Licensed under GPLv2 or any later version
// Refer to the license.txt file included.

#pragma once

#include <array>
#include <chrono>
#include <cstddef>
#include <mutex>
#include "common/common_types.h"
#include "common/thread.h"

namespace Core {

class FrameLimiter {
public:
    using Clock = std::chrono::high_resolution_clock;

    void DoFrameLimiting(std::chrono::microseconds current_system_time_us);

private:
    /// Emulated system time (in microseconds) at the last limiter invocation
    std::chrono::microseconds previous_system_time_us{0};
    /// Walltime at the last limiter invocation
    Clock::time_point previous_walltime = Clock::now();

    /// Accumulated difference between walltime and emulated time
    std::chrono::microseconds frame_limiting_delta_err{0};
};

/**
 * Class to manage and query performance/timing statistics. All public functions of this class are
 * thread-safe unless stated otherwise.
 */
class PerfStats {
public:
    using Clock = std::chrono::high_resolution_clock;

    struct Results {
        /// System FPS (LCD VBlanks) in Hz
        double system_fps;
        /// Game FPS (GSP frame submissions) in Hz
        double game_fps;
        /// Ratio of walltime / emulated time elapsed
        double emulation_speed;
    };

    void BeginSystemFrame();
    void EndSystemFrame();
    void EndGameFrame();

    Results GetAndResetStats(std::chrono::microseconds current_system_time_us);

    /**
     * Gets the ratio between walltime and the emulated time of the previous system frame. This is
     * useful for scaling inputs or outputs moving between the two time domains.
     */
    double GetLastFrameTimeScale() const;

private:
    FrameLimiter frame_limiter;

    /// Point when the cumulative counters were reset
    Clock::time_point reset_point = Clock::now();
    /// System time when the cumulative counters were reset
    std::chrono::microseconds reset_point_system_us;

    /// Cumulative number of system frames (LCD VBlanks) presented since last reset
    u32 system_frames = 0;
    /// Cumulative number of game frames (GSP frame submissions) since last reset
    u32 game_frames = 0;

    /// Point when the previous system frame ended
    Clock::time_point previous_frame_end = reset_point;
    /// Total visible duration (including frame-limiting, etc.) of the previous system frame
    Clock::duration previous_frame_length = Clock::duration::zero();

    /**
     * Frame-boundary-aligned samples for the rolling SPD/VPS estimates. Guest time advances in
     * bursts within a frame (limiter sleep batches ahead of heavy frames), so an estimate taken
     * over an arbitrary wall-clock window quantizes that staircase into several percent of
     * apparent speed noise. Estimates whose endpoints are frame boundaries cancel it exactly.
     */
    static constexpr std::size_t FRAME_SAMPLE_RING_SIZE = 128;
    struct FrameSample {
        Clock::time_point wall{};
        std::chrono::microseconds guest{};
    };
    std::array<FrameSample, FRAME_SAMPLE_RING_SIZE> frame_sample_ring{};
    std::size_t frame_sample_head = 0;
    std::size_t frame_sample_count = 0;
    std::array<Clock::time_point, FRAME_SAMPLE_RING_SIZE> game_sample_ring{};
    std::size_t game_sample_head = 0;
    std::size_t game_sample_count = 0;
    /// Guards the sample rings (written on the emu thread, read from the render/OSD thread)
    mutable std::mutex sample_mutex;
};

} // namespace Core

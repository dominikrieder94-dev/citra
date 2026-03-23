#include "egl_android.h"

#include <atomic>
#include <chrono>
#include <android/native_window_jni.h>
#ifdef ANDROID
#include <android/log.h>
#endif
#include <glad/glad.h>
#include <array>

#include "jni_common.h"
#include "video_core/renderer_base.h"
#include "video_core/video_core.h"

namespace {

#ifdef ANDROID

using PerfClock = std::chrono::steady_clock;
constexpr u64 PERF_LOG_INTERVAL_NS = 5'000'000'000ULL;

u64 PerfNowNs() {
    return std::chrono::duration_cast<std::chrono::nanoseconds>(
               PerfClock::now().time_since_epoch())
        .count();
}

double PerfNsToMs(u64 ns) {
    return static_cast<double>(ns) / 1'000'000.0;
}

void UpdateAtomicMax(std::atomic<u64>& target, u64 value) {
    u64 current = target.load(std::memory_order_relaxed);
    while (current < value &&
           !target.compare_exchange_weak(current, value, std::memory_order_relaxed,
                                         std::memory_order_relaxed)) {
    }
}

struct EglPerfStats {
    std::atomic<u64> last_log_ns{0};
    std::atomic<u64> try_present_calls{0};
    std::atomic<u64> initial_make_current{0};
    std::atomic<u64> stopped_skips{0};
    std::atomic<u64> renderer_true{0};
    std::atomic<u64> renderer_false{0};
    std::atomic<u64> try_present_total_ns{0};
    std::atomic<u64> try_present_max_ns{0};
    std::atomic<u64> renderer_total_ns{0};
    std::atomic<u64> renderer_max_ns{0};
    std::atomic<u64> swap_calls{0};
    std::atomic<u64> swap_total_ns{0};
    std::atomic<u64> swap_max_ns{0};
    std::atomic<u64> direct_swap_calls{0};
    std::atomic<u64> direct_swap_total_ns{0};
    std::atomic<u64> direct_swap_max_ns{0};
    std::atomic<u64> poll_events{0};
    std::atomic<u64> surface_recreates{0};
};

EglPerfStats g_egl_perf_stats;

void MaybeLogEglPerf(u64 now_ns) {
    u64 last_log_ns = g_egl_perf_stats.last_log_ns.load(std::memory_order_relaxed);
    if (last_log_ns != 0 && now_ns - last_log_ns < PERF_LOG_INTERVAL_NS) {
        return;
    }
    if (!g_egl_perf_stats.last_log_ns.compare_exchange_strong(last_log_ns, now_ns,
                                                              std::memory_order_relaxed,
                                                              std::memory_order_relaxed)) {
        return;
    }
    if (last_log_ns == 0) {
        return;
    }

    const auto try_present_calls =
        g_egl_perf_stats.try_present_calls.exchange(0, std::memory_order_relaxed);
    const auto initial_make_current =
        g_egl_perf_stats.initial_make_current.exchange(0, std::memory_order_relaxed);
    const auto stopped_skips =
        g_egl_perf_stats.stopped_skips.exchange(0, std::memory_order_relaxed);
    const auto renderer_true = g_egl_perf_stats.renderer_true.exchange(0, std::memory_order_relaxed);
    const auto renderer_false =
        g_egl_perf_stats.renderer_false.exchange(0, std::memory_order_relaxed);
    const auto try_present_total_ns =
        g_egl_perf_stats.try_present_total_ns.exchange(0, std::memory_order_relaxed);
    const auto try_present_max_ns =
        g_egl_perf_stats.try_present_max_ns.exchange(0, std::memory_order_relaxed);
    const auto renderer_total_ns =
        g_egl_perf_stats.renderer_total_ns.exchange(0, std::memory_order_relaxed);
    const auto renderer_max_ns =
        g_egl_perf_stats.renderer_max_ns.exchange(0, std::memory_order_relaxed);
    const auto swap_calls = g_egl_perf_stats.swap_calls.exchange(0, std::memory_order_relaxed);
    const auto swap_total_ns =
        g_egl_perf_stats.swap_total_ns.exchange(0, std::memory_order_relaxed);
    const auto swap_max_ns = g_egl_perf_stats.swap_max_ns.exchange(0, std::memory_order_relaxed);
    const auto direct_swap_calls =
        g_egl_perf_stats.direct_swap_calls.exchange(0, std::memory_order_relaxed);
    const auto direct_swap_total_ns =
        g_egl_perf_stats.direct_swap_total_ns.exchange(0, std::memory_order_relaxed);
    const auto direct_swap_max_ns =
        g_egl_perf_stats.direct_swap_max_ns.exchange(0, std::memory_order_relaxed);
    const auto poll_events = g_egl_perf_stats.poll_events.exchange(0, std::memory_order_relaxed);
    const auto surface_recreates =
        g_egl_perf_stats.surface_recreates.exchange(0, std::memory_order_relaxed);

    if (try_present_calls == 0 && direct_swap_calls == 0 && poll_events == 0) {
        return;
    }

    const double avg_try_present_ms =
        try_present_calls == 0 ? 0.0 : PerfNsToMs(try_present_total_ns) / try_present_calls;
    const double avg_renderer_ms =
        try_present_calls == 0 ? 0.0 : PerfNsToMs(renderer_total_ns) / try_present_calls;
    const double avg_swap_ms =
        swap_calls == 0 ? 0.0 : PerfNsToMs(swap_total_ns) / swap_calls;
    const double avg_direct_swap_ms =
        direct_swap_calls == 0 ? 0.0 : PerfNsToMs(direct_swap_total_ns) / direct_swap_calls;

    __android_log_print(
        ANDROID_LOG_INFO, "citra",
        "[Perf][EGL] window_ms=%.1f try_calls=%llu initial=%llu stopped=%llu renderer_true=%llu "
        "renderer_false=%llu avg_try_ms=%.3f max_try_ms=%.3f avg_renderer_ms=%.3f "
        "max_renderer_ms=%.3f swaps=%llu avg_swap_ms=%.3f max_swap_ms=%.3f "
        "direct_swaps=%llu avg_direct_swap_ms=%.3f max_direct_swap_ms=%.3f poll_events=%llu "
        "surface_recreates=%llu",
        PerfNsToMs(now_ns - last_log_ns), static_cast<unsigned long long>(try_present_calls),
        static_cast<unsigned long long>(initial_make_current),
        static_cast<unsigned long long>(stopped_skips),
        static_cast<unsigned long long>(renderer_true),
        static_cast<unsigned long long>(renderer_false), avg_try_present_ms,
        PerfNsToMs(try_present_max_ns), avg_renderer_ms, PerfNsToMs(renderer_max_ns),
        static_cast<unsigned long long>(swap_calls), avg_swap_ms, PerfNsToMs(swap_max_ns),
        static_cast<unsigned long long>(direct_swap_calls), avg_direct_swap_ms,
        PerfNsToMs(direct_swap_max_ns), static_cast<unsigned long long>(poll_events),
        static_cast<unsigned long long>(surface_recreates));
}

void RecordTryPresentStopped() {
    const auto now_ns = PerfNowNs();
    g_egl_perf_stats.try_present_calls.fetch_add(1, std::memory_order_relaxed);
    g_egl_perf_stats.stopped_skips.fetch_add(1, std::memory_order_relaxed);
    MaybeLogEglPerf(now_ns);
}

void RecordTryPresentInitialMakeCurrent() {
    g_egl_perf_stats.initial_make_current.fetch_add(1, std::memory_order_relaxed);
}

void RecordTryPresentResult(u64 total_ns, u64 renderer_ns, bool presented, u64 swap_ns) {
    const auto now_ns = PerfNowNs();
    g_egl_perf_stats.try_present_calls.fetch_add(1, std::memory_order_relaxed);
    g_egl_perf_stats.try_present_total_ns.fetch_add(total_ns, std::memory_order_relaxed);
    g_egl_perf_stats.renderer_total_ns.fetch_add(renderer_ns, std::memory_order_relaxed);
    UpdateAtomicMax(g_egl_perf_stats.try_present_max_ns, total_ns);
    UpdateAtomicMax(g_egl_perf_stats.renderer_max_ns, renderer_ns);
    if (presented) {
        g_egl_perf_stats.renderer_true.fetch_add(1, std::memory_order_relaxed);
        g_egl_perf_stats.swap_calls.fetch_add(1, std::memory_order_relaxed);
        g_egl_perf_stats.swap_total_ns.fetch_add(swap_ns, std::memory_order_relaxed);
        UpdateAtomicMax(g_egl_perf_stats.swap_max_ns, swap_ns);
    } else {
        g_egl_perf_stats.renderer_false.fetch_add(1, std::memory_order_relaxed);
    }
    MaybeLogEglPerf(now_ns);
}

void RecordDirectSwap(u64 swap_ns) {
    const auto now_ns = PerfNowNs();
    g_egl_perf_stats.direct_swap_calls.fetch_add(1, std::memory_order_relaxed);
    g_egl_perf_stats.direct_swap_total_ns.fetch_add(swap_ns, std::memory_order_relaxed);
    UpdateAtomicMax(g_egl_perf_stats.direct_swap_max_ns, swap_ns);
    MaybeLogEglPerf(now_ns);
}

void RecordPollEvents() {
    const auto now_ns = PerfNowNs();
    g_egl_perf_stats.poll_events.fetch_add(1, std::memory_order_relaxed);
    MaybeLogEglPerf(now_ns);
}

void RecordSurfaceRecreate() {
    g_egl_perf_stats.surface_recreates.fetch_add(1, std::memory_order_relaxed);
}

#else

u64 PerfNowNs() {
    return 0;
}

void RecordTryPresentStopped() {}
void RecordTryPresentInitialMakeCurrent() {}
void RecordTryPresentResult(u64, u64, bool, u64) {}
void RecordDirectSwap(u64) {}
void RecordPollEvents() {}
void RecordSurfaceRecreate() {}

#endif

} // namespace

static constexpr std::array<EGLint, 15> egl_attribs{EGL_SURFACE_TYPE,
                                                    EGL_WINDOW_BIT,
                                                    EGL_RENDERABLE_TYPE,
                                                    EGL_OPENGL_ES3_BIT_KHR,
                                                    EGL_BLUE_SIZE,
                                                    8,
                                                    EGL_GREEN_SIZE,
                                                    8,
                                                    EGL_RED_SIZE,
                                                    8,
                                                    EGL_DEPTH_SIZE,
                                                    0,
                                                    EGL_STENCIL_SIZE,
                                                    0,
                                                    EGL_NONE};
static constexpr std::array<EGLint, 5> egl_empty_attribs{EGL_WIDTH, 1, EGL_HEIGHT, 1, EGL_NONE};
static constexpr std::array<EGLint, 4> egl_context_attribs{EGL_CONTEXT_CLIENT_VERSION, 3, EGL_NONE};

class SharedContext_Android {
public:
    SharedContext_Android(EGLDisplay egl_display, EGLConfig egl_config,
                          EGLContext egl_share_context)
        : egl_display{egl_display}, egl_surface{eglCreatePbufferSurface(egl_display, egl_config,
                                                                        egl_empty_attribs.data())},
          egl_context{eglCreateContext(egl_display, egl_config, egl_share_context,
                                       egl_context_attribs.data())} {}

    ~SharedContext_Android() {
        if (!eglDestroySurface(egl_display, egl_surface)) {
            LOG_CRITICAL(Frontend, "eglDestroySurface() failed");
        }

        if (!eglDestroyContext(egl_display, egl_context)) {
            LOG_CRITICAL(Frontend, "eglDestroySurface() failed");
        }
    }

    void MakeCurrent() {
        eglMakeCurrent(egl_display, egl_surface, egl_surface, egl_context);
    }

    void DoneCurrent() {
        eglMakeCurrent(egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }

private:
    EGLDisplay egl_display;
    EGLSurface egl_surface;
    EGLContext egl_context;
};

EGLAndroid::EGLAndroid(bool use_shared_context) : use_shared_context(use_shared_context) {}

bool EGLAndroid::Initialize(ANativeWindow* surface) {
    host_window = surface;
    egl_display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (!egl_display) {
        // Error: eglGetDisplay() failed
        return false;
    }

    EGLint egl_major, egl_minor;
    if (!eglInitialize(egl_display, &egl_major, &egl_minor)) {
        // Error: eglInitialize() failed
        return false;
    }

    EGLint num_configs;
    if (!eglChooseConfig(egl_display, egl_attribs.data(), &egl_config, 1, &num_configs)) {
        // Error: couldn't get an EGL visual config
        return false;
    }

    egl_context = eglCreateContext(egl_display, egl_config, 0, egl_context_attribs.data());
    if (egl_context == EGL_NO_CONTEXT) {
        return false;
    }

    if (use_shared_context) {
        core_context =
            std::make_unique<SharedContext_Android>(egl_display, egl_config, egl_context);
    } else {
        presenting_state = PresentingState::Stopped;
    }

    CreateWindowSurface();

    return gladLoadGLES2Loader((GLADloadproc)eglGetProcAddress);
}

void EGLAndroid::UpdateSurface(ANativeWindow* surface) {
    new_window = surface;
    StopPresenting();
}

void EGLAndroid::UpdateWindow() {
    window_width = ANativeWindow_getWidth(host_window);
    window_height = ANativeWindow_getHeight(host_window);
    safe_inset_left = static_cast<u32>(NativeLibrary::GetSafeInsetLeft());
    safe_inset_top = static_cast<u32>(NativeLibrary::GetSafeInsetTop());
    safe_inset_right = static_cast<u32>(NativeLibrary::GetSafeInsetRight());
    safe_inset_bottom = static_cast<u32>(NativeLibrary::GetSafeInsetBottom());
    UpdateLayout();
}

void EGLAndroid::UpdateLayout() {
    UpdateFramebufferLayout(window_width, window_height);
}

void EGLAndroid::CreateWindowSurface() {
    if (!host_window) {
        return;
    }
    RecordSurfaceRecreate();
    EGLint format;
    eglGetConfigAttrib(egl_display, egl_config, EGL_NATIVE_VISUAL_ID, &format);
    ANativeWindow_setBuffersGeometry(host_window, 0, 0, format);
    egl_surface = eglCreateWindowSurface(egl_display, egl_config, host_window, nullptr);
    eglQuerySurface(egl_display, egl_surface, EGL_WIDTH, &window_width);
    eglQuerySurface(egl_display, egl_surface, EGL_HEIGHT, &window_height);
    eglSurfaceAttrib(egl_display, egl_surface, EGL_SWAP_BEHAVIOR, EGL_BUFFER_DESTROYED);

    // screen
    safe_inset_left = static_cast<u32>(NativeLibrary::GetSafeInsetLeft());
    safe_inset_top = static_cast<u32>(NativeLibrary::GetSafeInsetTop());
    safe_inset_right = static_cast<u32>(NativeLibrary::GetSafeInsetRight());
    safe_inset_bottom = static_cast<u32>(NativeLibrary::GetSafeInsetBottom());
    scale_density = static_cast<float>(NativeLibrary::GetScaleDensity());

    MakeCurrent();
    UpdateLayout();
}

void EGLAndroid::DestroyWindowSurface() {
    if (!egl_surface) {
        return;
    }
    if (eglGetCurrentSurface(EGL_DRAW) == egl_surface) {
        eglMakeCurrent(egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }
    if (!eglDestroySurface(egl_display, egl_surface)) {
        // Could not destroy window surface
    }
    egl_surface = EGL_NO_SURFACE;
}

void EGLAndroid::DestroyContext() {
    if (!egl_context) {
        return;
    }
    if (eglGetCurrentContext() == egl_context) {
        eglMakeCurrent(egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }
    if (!eglDestroyContext(egl_display, egl_context)) {
        // Could not destroy drawing context
    }
    if (!eglTerminate(egl_display)) {
        // Could not destroy display connection
    }
    egl_context = EGL_NO_CONTEXT;
    egl_display = EGL_NO_DISPLAY;
}

EGLAndroid::~EGLAndroid() {
    core_context.reset();
    DestroyWindowSurface();
    DestroyContext();
}

void EGLAndroid::TryPresenting() {
    const auto total_start_ns = PerfNowNs();
    if (presenting_state != PresentingState::Running) {
        if (presenting_state == PresentingState::Initial) {
            RecordTryPresentInitialMakeCurrent();
            eglMakeCurrent(egl_display, egl_surface, egl_surface, egl_context);
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
            presenting_state = PresentingState::Running;
        } else {
            RecordTryPresentStopped();
            return;
        }
    }
    const auto renderer_start_ns = PerfNowNs();
    const bool presented = VideoCore::Renderer()->TryPresent();
    const auto renderer_ns = PerfNowNs() - renderer_start_ns;
    u64 swap_ns = 0;
    if (presented) {
        const auto swap_start_ns = PerfNowNs();
        eglSwapBuffers(egl_display, egl_surface);
        swap_ns = PerfNowNs() - swap_start_ns;
    }
    RecordTryPresentResult(PerfNowNs() - total_start_ns, renderer_ns, presented, swap_ns);
}

void EGLAndroid::StopPresenting() {
    if (presenting_state == PresentingState::Running) {
        eglMakeCurrent(egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }
    presenting_state = PresentingState::Stopped;
}

void EGLAndroid::SwapBuffers() {
    const auto swap_start_ns = PerfNowNs();
    eglSwapBuffers(egl_display, egl_surface);
    RecordDirectSwap(PerfNowNs() - swap_start_ns);
}

void EGLAndroid::PollEvents() {
    if (!new_window) {
        return;
    }
    RecordPollEvents();

    host_window = new_window;
    new_window = nullptr;
    DestroyWindowSurface();
    CreateWindowSurface();
    VideoCore::Renderer()->ResetPresent();
    if (use_shared_context) {
        presenting_state = PresentingState::Initial;
    }
}

void EGLAndroid::MakeCurrent() {
    if (use_shared_context) {
        core_context->MakeCurrent();
    } else {
        eglMakeCurrent(egl_display, egl_surface, egl_surface, egl_context);
    }
}

void EGLAndroid::DoneCurrent() {
    if (use_shared_context) {
        core_context->DoneCurrent();
    } else {
        eglMakeCurrent(egl_display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }
}

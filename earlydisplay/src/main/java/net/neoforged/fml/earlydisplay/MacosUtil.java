/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.fml.earlydisplay;

import org.lwjgl.glfw.GLFWNativeCocoa;
import org.lwjgl.system.JNI;
import org.lwjgl.system.macosx.ObjCRuntime;

/**
 * Provides access to macOS window state by sending messages directly through the Objective-C runtime.
 *
 * <p>The native function, window, and selector handles are pointers, which LWJGL represents as {@code long} values.
 */
final class MacosUtil {
    /**
     * AppKit's {@code NSWindowStyleMaskFullScreen}, defined as {@code 1 << 14} in {@code <AppKit/NSWindow.h>}.
     *
     * @see <a href="https://developer.apple.com/documentation/appkit/nswindow/stylemask-swift.struct/fullscreen">Apple's
     *      NSWindow.StyleMask.fullScreen documentation</a>
     */
    private static final long NS_FULL_SCREEN_WINDOW_MASK = 1L << 14;

    // Objective-C method calls are dispatched through objc_msgSend(receiver, selector, arguments...)
    private static final long OBJC_MSG_SEND = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");

    // selectors identify the Objective-C methods passed to objc_msgSend
    private static final long STYLE_MASK = ObjCRuntime.sel_registerName("styleMask");

    private MacosUtil() {}

    static boolean isFullscreen(long windowHandle) {
        long nsWindow = GLFWNativeCocoa.glfwGetCocoaWindow(windowHandle);
        return nsWindow != 0L && (getStyleMask(nsWindow) & NS_FULL_SCREEN_WINDOW_MASK) != 0L;
    }

    private static long getStyleMask(long nsWindow) {
        // Equivalent to: [nsWindow styleMask]
        // NSUInteger maps to C long (the J return type in LWJGL's JNI signatures), not a pointer.
        return JNI.invokePPJ(nsWindow, STYLE_MASK, OBJC_MSG_SEND);
    }
}

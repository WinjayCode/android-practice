package com.winjay.practice.technical_solution.skip_ad;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.winjay.practice.utils.LogUtil;

/**
 * Skip Ad
 *
 * @author Winjay
 * @date 2023-11-09
 */
public class SkipAdAccessibilityService extends AccessibilityService {
    private static final String TAG = SkipAdAccessibilityService.class.getSimpleName();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            LogUtil.d(TAG);
            if (rootNode == null) {
                return;
            }

            // 在这里根据具体的app和广告类型，找到跳过广告的按钮或元素并点击
            // 可以通过遍历节点树，查找特定的文本、ID、类名等属性来定位跳过按钮
            // 例如，通过节点的文本内容来查找：
            clickSkipButton(rootNode, "跳过");
        }
    }

    private void clickSkipButton(AccessibilityNodeInfo node, String targetText) {
        LogUtil.d(TAG);
        if (node.getChildCount() == 0) {
            if (node.getText() != null && node.getText().toString().contains(targetText)) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                clickSkipButton(node.getChild(i), targetText);
            }
        }
    }

    @Override
    public void onInterrupt() {
        // 当服务中断时的回调方法
    }
}

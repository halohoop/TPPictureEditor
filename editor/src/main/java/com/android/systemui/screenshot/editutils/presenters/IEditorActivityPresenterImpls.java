/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * IEditorActivityPresenterImpls.java
 *
 * Implements of IEditorActivityPresenter
 *
 * Author huanghaiqi, Created at 2016-10-02
 *
 * Ver 1.0, 2016-10-02, huanghaiqi, Create file.
 */

package com.android.systemui.screenshot.editutils.presenters;

import com.android.systemui.screenshot.editutils.widgets.AnimationEndMark;

import java.util.ArrayList;
import java.util.List;

/**
 * @hide
 */
public class IEditorActivityPresenterImpls implements IEditorActivityPresenter {

    private final List<AnimationEndMark> needAnimationEnds;

    public IEditorActivityPresenterImpls() {
        needAnimationEnds = new ArrayList<>();
    }

    @Override
    public void needAnimationEndToAct(AnimationEndMark animationEndMark) {
        needAnimationEnds.add(animationEndMark);
    }

    @Override
    public boolean isAllAnimationEnd() {
        int endAnimationCount = 0;
        for (int i = 0; i < needAnimationEnds.size(); i++) {
            AnimationEndMark animationEndMark = needAnimationEnds.get(i);
            if (animationEndMark.isAnimationEnd()) {
                endAnimationCount++;
            }
        }
        if (endAnimationCount == needAnimationEnds.size()) {
            return true;
        } else {
            return false;
        }
    }
}
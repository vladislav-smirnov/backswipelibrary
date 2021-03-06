/*
 * Copyright Notice:
 *
 * Copyright (C) Vladislav Smirnov, 2018.
 * All Rights Reserved.
 *
 * The reproduction, transmission or use of this document or its contents is
 * not permitted without express written authority.
 * Offenders will be liable for damages. All rights, including rights created
 * by patent grant or registration of a utility model or design, are reserved.
 *
 */
package io.github.airdaydreamers.backswipelibrary.listeners;

import androidx.fragment.app.Fragment;

/*
  Created by Vladislav Smirnov on 5/4/2018.
  sivdead@gmail.com
 */

/**
 * Interface definition for a callback to be invoked when a fragment is added.
 */
public interface OnFragmentChangeListener {
    /**
     * Called when a fragment has been added.
     *
     * @param fromFragment, The fragment that was before.
     * @param toFragment    The fragment that was added for transition.
     */
    void onFragmentAdded(Fragment fromFragment, Fragment toFragment);
}

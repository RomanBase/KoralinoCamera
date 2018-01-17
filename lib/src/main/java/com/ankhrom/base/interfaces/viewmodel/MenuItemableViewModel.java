package com.ankhrom.base.interfaces.viewmodel;

import com.ankhrom.base.model.ToolbarItemModel;

public interface MenuItemableViewModel {

    /**
     * @return list of menu items (toolbar menu icons)
     */
    ToolbarItemModel[] getMenuItems();
}

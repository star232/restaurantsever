package com.pthhack22.myrestaurantserver.common;

import com.pthhack22.myrestaurantserver.model.CategoryModel;
import com.pthhack22.myrestaurantserver.model.FoodModel;
import com.pthhack22.myrestaurantserver.model.ServerUserModel;

public class Common {
    public static final String SERVER_REF ="Server";
    public static final String CATEGORY_REF = "Category";
    public static ServerUserModel currentSeverUser;
    public static CategoryModel categorySelected;
    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static FoodModel selectedFood;
}

package com.meatchop.widget;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCMenuItemStockAvlDetails;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TMCMenuItemCatalog {

    private final String TAG = "TMCMenuItemCatalog";
    private static TMCMenuItemCatalog mItemCatalogOne = null;

    private HashMap cartMenuItemMap = null;       // {String(Key) -- TMCMenuItem()}
    private HashMap menuItemMap = null;       // {String(Key) -- TMCMenuItem()}
    private HashMap<String, ArrayList<TMCMenuItem>> tmcSubctgyMenuItemMap = null;  // {String(tmcsubctgy) -- ArrayList<TMCMenuItem>}
    private HashMap uniqueCodeMenuItemKeyMap = null; // {String(uniquecode) -- String(tmcmenuitemkey)}

    private HashMap<String, TMCMenuItemStockAvlDetails> tmcMenuItemStockAvlDetailsMap = null; //{String(menuitemkey -- TMCMenuItemStockAvlDetails}

    static { mItemCatalogOne = new TMCMenuItemCatalog(); }

    private TMCMenuItemCatalog() { }

    public static TMCMenuItemCatalog getInstance() { return mItemCatalogOne; }

    public int menuItemSize() { return (menuItemMap == null) ? 0 : menuItemMap.size(); }
    public int cartMenuItemMapsize() { return (cartMenuItemMap == null) ? 0 : cartMenuItemMap.size(); }
    public int tmcSubctgyMenuItemSize() { return (tmcSubctgyMenuItemMap == null) ? 0 : tmcSubctgyMenuItemMap.size(); }

    public int tmcSubctgyMenuItemSize(String subctgykey) {
        ArrayList<TMCMenuItem> menuItemList = (tmcSubctgyMenuItemMap == null) ? null : tmcSubctgyMenuItemMap.get(subctgykey);
        int size = (menuItemList == null) ? 0 : menuItemList.size();
        return size;
    }

    public int getCartCount() {
        int count = 0;
        if (cartMenuItemMapsize() <= 0) {
            return count;
        }
        ArrayList<TMCMenuItem> menuItems = getCartMenuItemList();
        for (int i=0; i<menuItems.size(); i++) {
            TMCMenuItem tmcMenuItem = menuItems.get(i);
            count = count + tmcMenuItem.getSelectedqty();
        }
        return count;
    }

    public double getCartAmount() {
        double totalamount = 0;
        if (cartMenuItemMapsize() <= 0) {
            return totalamount;
        }
        ArrayList<TMCMenuItem> menuItems = getCartMenuItemList();
        for (int i=0; i<menuItems.size(); i++) {
            TMCMenuItem tmcMenuItem = menuItems.get(i);
            double tmcprice = tmcMenuItem.getSelectedqty() * tmcMenuItem.getTmcprice();
            totalamount = totalamount + tmcprice;
        }
        return totalamount;
    }

    public void addMenuItem(TMCMenuItem menuItem) {
        if (menuItemMap == null) {
            menuItemMap = new HashMap<String, TMCMenuItem>();
        }
        if (tmcSubctgyMenuItemMap == null) {
            tmcSubctgyMenuItemMap = new HashMap<String, ArrayList<TMCMenuItem>>();
        }
        menuItemMap.put(menuItem.getKey(), menuItem);
        addTMCMenuItemandSubctgy(menuItem);
        addUniqueCodeAndMenuItemKey(menuItem);
    }

    private void addTMCMenuItemandSubctgy(TMCMenuItem tmcMenuItem) {
        if (tmcSubctgyMenuItemMap == null) {
            tmcSubctgyMenuItemMap = new HashMap<String, ArrayList<TMCMenuItem>>();
        }
        String tmcsubctgykey = tmcMenuItem.getTmcsubctgykey();
        ArrayList<TMCMenuItem> tmcMenuItems = (ArrayList<TMCMenuItem>) tmcSubctgyMenuItemMap.get(tmcsubctgykey);
        if (tmcMenuItems == null) {
            tmcMenuItems = new ArrayList<>();
            tmcMenuItems.add(tmcMenuItem);
            tmcSubctgyMenuItemMap.put(tmcsubctgykey, tmcMenuItems);
        } else {
            tmcMenuItems.add(tmcMenuItem);
        }
    }

    private void addUniqueCodeAndMenuItemKey(TMCMenuItem tmcMenuItem) {
        if (uniqueCodeMenuItemKeyMap == null) {
            uniqueCodeMenuItemKeyMap = new HashMap<String, String>();
        }
        uniqueCodeMenuItemKeyMap.put(tmcMenuItem.getItemuniquecode(), tmcMenuItem.getKey());
    }

    public void updateMenuItemInCart(TMCMenuItem menuItem) {
        if (cartMenuItemMap == null) {
            cartMenuItemMap = new HashMap<String, TMCMenuItem>();
        }
        if (menuItem.getSelectedqty() > 0) {
            cartMenuItemMap.put(menuItem.getKey(), menuItem);
        } else {
            cartMenuItemMap.remove(menuItem.getKey());
        }
     // Log.d("TMCMenuItemCatalog", "cartMenuItemMap "+cartMenuItemMap);
    }

    public void removeMenuItemInCart(String menuitemkey) {
        if ((cartMenuItemMap == null) || (cartMenuItemMap.size() <= 0)) {
            return;
        }
        cartMenuItemMap.remove(menuitemkey);
    }

    public void clearCartTMCMenuItems() {
        Log.d(TAG, "cart items cleared");
        if (cartMenuItemMap != null) {
            cartMenuItemMap.clear();
            cartMenuItemMap = null;
        }
        clearSlotDetails();
    }

    public void clearMenuItems() {
        if (menuItemMap != null) {
            menuItemMap.clear();
            menuItemMap = null;
        }
        if (tmcSubctgyMenuItemMap != null) {
            tmcSubctgyMenuItemMap.clear();
            tmcSubctgyMenuItemMap = null;
        }

        if (uniqueCodeMenuItemKeyMap != null) {
            uniqueCodeMenuItemKeyMap.clear();
            uniqueCodeMenuItemKeyMap = null;
        }

     /* if (tmcMenuItemStockAvlDetailsMap != null) {
            tmcMenuItemStockAvlDetailsMap.clear();
            tmcMenuItemStockAvlDetailsMap = null;
        } */

        clearSlotDetails();
    }

    public void clearSlotDetails() {
        setSelecteddeliveryslotkey("");
        setSelectedDeliverySlot("");
        setSelectedSlotSessionType("");
    }

    public void clear() {
        if (cartMenuItemMap != null) {
            Log.d("TMCMenuItemCataglog", "before clearing MenuItemCatalog");
            cartMenuItemMap.clear();
            cartMenuItemMap = null;
        }
        if (menuItemMap != null) {
            menuItemMap.clear();
            menuItemMap = null;
        }
        if (tmcSubctgyMenuItemMap != null) {
            tmcSubctgyMenuItemMap.clear();
            tmcSubctgyMenuItemMap = null;
        }
        if (uniqueCodeMenuItemKeyMap != null) {
            uniqueCodeMenuItemKeyMap.clear();
            uniqueCodeMenuItemKeyMap = null;
        }

        if (tmcMenuItemStockAvlDetailsMap != null) {
            tmcMenuItemStockAvlDetailsMap.clear();
            tmcMenuItemStockAvlDetailsMap = null;
        }
        clearSlotDetails();
    }

    public ArrayList<TMCMenuItem> getMenuItemList() {
        if (menuItemSize() == 0) { return null; }
        Object[] keys = menuItemMap.keySet().toArray();
        ArrayList<TMCMenuItem> menuItemList = new ArrayList<TMCMenuItem>();
        for (int i=0; i<menuItemMap.size(); i++) {
            TMCMenuItem menuItem = (TMCMenuItem) menuItemMap.get(keys[i]);
            menuItemList.add(menuItem);
        }
        return menuItemList;
    }

    public ArrayList<TMCMenuItem> getMenuItemList(String tmcsubctgykey) {
        if ((tmcSubctgyMenuItemMap == null) || (tmcSubctgyMenuItemMap.size() <= 0)) { return null; }
        Object[] keys = tmcSubctgyMenuItemMap.keySet().toArray();
        ArrayList<TMCMenuItem> menuItemList = tmcSubctgyMenuItemMap.get(tmcsubctgykey);
        return menuItemList;
    }

    public HashMap<String, ArrayList<TMCMenuItem>> getTMCMenuItemsFromUniqueCode(String uniquecodes) {
        if ((uniqueCodeMenuItemKeyMap == null) || (uniqueCodeMenuItemKeyMap.size() <= 0)) {
            return null;
        }
        if ((menuItemMap == null) || (menuItemMap.size() <= 0)) {
            return null;
        }
        HashMap<String, ArrayList<TMCMenuItem>> tmcSubCtgyMenuItemMap = new HashMap<String, ArrayList<TMCMenuItem>>();
        String[] codelist = uniquecodes.split(",");
        for (int i=0; i<codelist.length; i++) {
            String code = codelist[i].trim();
            String menuitemkey = (String) uniqueCodeMenuItemKeyMap.get(code);
            TMCMenuItem tmcMenuItem = (menuitemkey == null) ? null : (TMCMenuItem) menuItemMap.get(menuitemkey);
            ArrayList<TMCMenuItem> menuItemArrayList = tmcSubCtgyMenuItemMap.get(tmcMenuItem.getTmcsubctgykey());
            if (menuItemArrayList == null) {
                menuItemArrayList = new ArrayList<TMCMenuItem>();
                menuItemArrayList.add(tmcMenuItem);
                tmcSubCtgyMenuItemMap.put(tmcMenuItem.getTmcsubctgykey(), menuItemArrayList);
            } else {
                menuItemArrayList.add(tmcMenuItem);
            }
        }

        return tmcSubCtgyMenuItemMap;
    }

    public ArrayList<TMCMenuItem> getCartMenuItemList() {
        if (cartMenuItemMapsize() == 0) { return null; }
        Object[] keys = cartMenuItemMap.keySet().toArray();
        ArrayList<TMCMenuItem> menuItemList = new ArrayList<TMCMenuItem>();
        for (int i=0; i<cartMenuItemMap.size(); i++) {
            TMCMenuItem menuItem = (TMCMenuItem) cartMenuItemMap.get(keys[i]);
            if ((menuItem != null) && (menuItem.getSelectedqty() > 0)) {
                menuItemList.add(menuItem);
            }
        }

        return menuItemList;
    }

    public int getMarinadeSelectedQty(String menuitemkey) {
        if ((cartMenuItemMap == null) || (cartMenuItemMap.size() <= 0)) {
            return 0;
        }
        Object[] keys = cartMenuItemMap.keySet().toArray();
        int selqty = 0;
        for (int i=0; i<keys.length; i++) {
            String cartmenuitemkey = (String) keys[i];
            if (cartmenuitemkey.contains(menuitemkey)) {
                TMCMenuItem tmcMenuItem = (TMCMenuItem) cartMenuItemMap.get(cartmenuitemkey);
                selqty = selqty + tmcMenuItem.getSelectedqty();
            }
        }

        return selqty;
    }

    public int getCutsAndWeightSelectedQty(String menuitemkey) {
        if ((cartMenuItemMap == null) || (cartMenuItemMap.size() <= 0)) {
            return 0;
        }
        Object[] keys = cartMenuItemMap.keySet().toArray();
        int selqty = 0;
        for (int i=0; i<keys.length; i++) {
            String cartmenuitemkey = (String) keys[i];
            if (cartmenuitemkey.contains(menuitemkey)) {
                TMCMenuItem tmcMenuItem = (TMCMenuItem) cartMenuItemMap.get(cartmenuitemkey);
                selqty = selqty + tmcMenuItem.getSelectedqty();
            }
        }
        return selqty;
    }

    public TMCMenuItem getCartTMCMenuItem(String key) {
        if (cartMenuItemMapsize() == 0) { return null; }
        return (TMCMenuItem)cartMenuItemMap.get(key);
    }

    public TMCMenuItem getTMCMenuItem(String key) {
        if (menuItemSize() == 0) { return null; }
        return (TMCMenuItem)menuItemMap.get(key);
    }

    public TMCMenuItem getTMCMenuItemForInventory(String inventorykey) {
        if (menuItemSize() == 0) { return null; }
        Object[] keys = menuItemMap.keySet().toArray();
        TMCMenuItem tmcMenuItem = null;
        for (int i=0; i<keys.length; i++) {
            String menuitemkey = (String) keys[i];
            if (menuitemkey.contains(inventorykey)) {
                tmcMenuItem = (TMCMenuItem) menuItemMap.get(menuitemkey);
                return tmcMenuItem;
            }
        }
        return tmcMenuItem;
    }

    private String slotsessiontype = "";
    private String selecteddeliveryslot = "";
    private String selecteddeliveryslotkey = "";
    public String getSelectedSlotSessionType() {
        return slotsessiontype;
    }

    public void setSelectedSlotSessionType(String slotsessiontype) {
        this.slotsessiontype = slotsessiontype;
    }

    public String getSelectedDeliverySlot() {
        return selecteddeliveryslot;
    }
    public void setSelectedDeliverySlot(String selecteddeliveryslot) {
        this.selecteddeliveryslot = selecteddeliveryslot;
    }
    public String getSelecteddeliveryslotkey() {
        return selecteddeliveryslotkey;
    }
    public void setSelecteddeliveryslotkey(String selecteddeliveryslotkey) {
        this.selecteddeliveryslotkey = selecteddeliveryslotkey;
    }

    public boolean isMeatProductsAvailableInCart(String freshproducectgykey) {
        if (cartMenuItemMapsize() <= 0) {
            return false;
        }
        if ((freshproducectgykey == null) || (TextUtils.isEmpty(freshproducectgykey))) {
            return true;
        }
        ArrayList<TMCMenuItem> menuItems = getCartMenuItemList();
        int freshproducecount = 0;
        for (int i=0; i<menuItems.size(); i++) {
            TMCMenuItem tmcMenuItem = menuItems.get(i);
            if (tmcMenuItem.getTmcctgykey().equalsIgnoreCase(freshproducectgykey)) {
                freshproducecount = freshproducecount + 1;
            }
        }
        if (menuItems.size() == freshproducecount) {
            return false;
        } else {
            return true;
        }
    }

    public void addMenuItemStockAvlDetails(TMCMenuItemStockAvlDetails menuItemStockAvlDetails) {
        if (tmcMenuItemStockAvlDetailsMap == null) {
            tmcMenuItemStockAvlDetailsMap = new HashMap<String, TMCMenuItemStockAvlDetails>();
        }
        tmcMenuItemStockAvlDetailsMap.put(menuItemStockAvlDetails.getMenuitemkey(), menuItemStockAvlDetails);
    }

    public TMCMenuItemStockAvlDetails getMenuItemStockAvlDetails(String menuitemkey) {
        if (tmcMenuItemStockAvlDetailsMap == null) {
            return null;
        }
        return tmcMenuItemStockAvlDetailsMap.get(menuitemkey);
    }

    public void clearMenuItemStockAvlDetails() {
        if (tmcMenuItemStockAvlDetailsMap != null) {
            tmcMenuItemStockAvlDetailsMap.clear();
            tmcMenuItemStockAvlDetailsMap = null;
        }
    }



}

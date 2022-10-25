package com.meatchop.widget;

import android.util.Log;

import com.meatchop.data.TMCMenuItem;
import com.meatchop.data.TMCSubCtgy;
import com.meatchop.data.TMCTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

public class TMCDataCatalog {

    private static TMCDataCatalog dataCatalogOne = null;

    private HashMap<String, TMCTile> tmcTileHashMap;  // {String(Key) -- TMCTile()}

    private ArrayList<String> tmcCtgyNameList;
    private TreeMap<String, ArrayList<TMCSubCtgy>> tmcCtgySubCtgyMap;  // {tmcctgyname -- Arraylist of subctgy}
    private HashMap<String, TMCSubCtgy> tmcSubCtgyMap;  // {tmcsubctgyname -- TMCSubCtgy}
    private HashMap<String, String> tmcSubCtgyKeyNameMap;  // {tmcsubctgykey -- tmcsubctgyname}

    private String mappedvendorkey;

    static { dataCatalogOne = new TMCDataCatalog(); }

    private TMCDataCatalog() { }

    public static TMCDataCatalog getInstance() { return dataCatalogOne; }

    public int tmcTilesSize() { return (tmcTileHashMap == null) ? 0 : tmcTileHashMap.size(); }
    public int tmcCtgySubCtgyMapSize() { return (tmcCtgySubCtgyMap == null) ? 0 : tmcCtgySubCtgyMap.size(); }

    public void setMappedvendorkey(String mappedvendorkey) { this.mappedvendorkey = mappedvendorkey; }
    public String getMappedvendorkey() { return this.mappedvendorkey; }

    public void addTMCTile(TMCTile tmcTile) {
        if (tmcTileHashMap == null) {
            tmcTileHashMap = new HashMap<String, TMCTile>();
        }
        tmcTileHashMap.put(tmcTile.getKey(), tmcTile);
    }

    public void clear() {
        if (tmcTileHashMap != null) {
            tmcTileHashMap.clear();
            tmcTileHashMap = null;
        }
        if (tmcCtgyNameList != null) {
            tmcCtgyNameList.clear();
            tmcCtgyNameList = null;
        }
        if (tmcCtgySubCtgyMap != null) {
            tmcCtgySubCtgyMap.clear();
            tmcCtgySubCtgyMap = null;
        }
        if (tmcSubCtgyMap != null) {
            tmcSubCtgyMap.clear();
            tmcSubCtgyMap = null;
        }
    }

    public HashMap<String, TMCTile> getTileMap() { return tmcTileHashMap; }
    public ArrayList<TMCTile> getTMCTileList() {
        if (tmcTilesSize() == 0) { return null; }
        Object[] keys = tmcTileHashMap.keySet().toArray();
        ArrayList<TMCTile> tmcTileList = new ArrayList<TMCTile>();
        for (int i=0; i<tmcTileHashMap.size(); i++) {
            TMCTile menuItem = (TMCTile) tmcTileHashMap.get(keys[i]);
            tmcTileList.add(menuItem);
        }
        return tmcTileList;
    }

    public void clearTMCSubCtgyItems() {
        if (tmcCtgyNameList != null) {
            tmcCtgyNameList.clear();
            tmcCtgyNameList = null;
        }
        if (tmcCtgySubCtgyMap != null) {
            tmcCtgySubCtgyMap.clear();
            tmcCtgySubCtgyMap = null;
        }
        if (tmcSubCtgyMap != null) {
            tmcSubCtgyMap.clear();
            tmcSubCtgyMap = null;
        }
        if (tmcSubCtgyKeyNameMap != null) {
            tmcSubCtgyKeyNameMap.clear();
            tmcSubCtgyKeyNameMap = null;
        }
    }

    public void addTMCCtgyAndSubctgy(String tmcctgyname, TMCSubCtgy tmcSubCtgy) {
        if (tmcCtgyNameList == null) {
            tmcCtgyNameList = new ArrayList<String>();
        }
        if (tmcCtgySubCtgyMap == null) {
            tmcCtgySubCtgyMap = new TreeMap<String, ArrayList<TMCSubCtgy>>();
        }
        if (tmcSubCtgyMap == null) {
            tmcSubCtgyMap = new HashMap<String, TMCSubCtgy>();
        }
        if (tmcSubCtgyKeyNameMap == null) {
            tmcSubCtgyKeyNameMap = new HashMap<String, String>();
        }
        if (!tmcCtgyNameList.contains(tmcctgyname)) {
            tmcCtgyNameList.add(tmcctgyname);
        }

        tmcSubCtgyMap.put(tmcSubCtgy.getSubctgyname(), tmcSubCtgy);
        tmcSubCtgyKeyNameMap.put(tmcSubCtgy.getSubctgykey(), tmcSubCtgy.getSubctgyname());
        ArrayList<TMCSubCtgy> subctgylist = tmcCtgySubCtgyMap.get(tmcctgyname);
        if (subctgylist == null) {
            subctgylist = new ArrayList<TMCSubCtgy>();
            subctgylist.add(tmcSubCtgy);
            tmcCtgySubCtgyMap.put(tmcctgyname, subctgylist);
        } else {
            subctgylist.add(tmcSubCtgy);
        }
    }

    public ArrayList<TMCSubCtgy> getTMCSubCtgyList(String tmcctgyname) {
        if ((tmcCtgySubCtgyMap == null) || (tmcCtgySubCtgyMap.size() <= 0)) {
            return null;
        }
        ArrayList<TMCSubCtgy> tmcSubCtgyList = tmcCtgySubCtgyMap.get(tmcctgyname);
        Collections.sort(tmcSubCtgyList);
        return tmcSubCtgyList;
    }

    public String getTMCSubCtgyName(String tmcsubctgykey) {
        if ((tmcSubCtgyKeyNameMap == null) || (tmcSubCtgyKeyNameMap.size() <= 0)) {
            return null;
        }
        return tmcSubCtgyKeyNameMap.get(tmcsubctgykey);
    }


    public ArrayList<String> getTmcCtgyNameList() { return this.tmcCtgyNameList; }
    public TreeMap<String, ArrayList<TMCSubCtgy>> getTmcCtgySubCtgyMap() { return this.tmcCtgySubCtgyMap; }
    public HashMap<String, TMCSubCtgy> getTmcSubctgyMap() { return this.tmcSubCtgyMap; }

}

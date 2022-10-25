package com.meatchop.data;


public class TMCSubCtgy implements Comparable<TMCSubCtgy> {

    private String subctgykey;
    private String subctgyname;
    private int displayno;

    public TMCSubCtgy(String subctgykey, int displayno, String subctgyname) {
        this.subctgykey = subctgykey;
        this.displayno = displayno;
        this.subctgyname = subctgyname;
    }

    public void setSubctgykey(String subctgykey) { this.subctgykey = subctgykey; }
    public String getSubctgykey() { return subctgykey; }

    public void setDisplayno(int displayno) { this.displayno = displayno; }
    public int getDisplayno() { return displayno; }

    public void setSubctgyname(String subctgyname) { this.subctgyname = subctgyname; }
    public String getSubctgyname() { return subctgyname; }

    @Override
    public int compareTo(TMCSubCtgy o) {
        try {
            /* For Ascending order do like this */
            return (this.displayno - o.displayno);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;

    }


}

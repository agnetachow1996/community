package com.nowcoder.community.entity;

public class Page {
    private int current = 1;
    //每页显示上限
    private int limit = 10;
    private int rows;
    private String path;

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }

    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit < 100) {
            this.limit = limit;
        }
    }

    public void setRows(int rows) {
        if(rows >= 0) {
            this.rows = rows;
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCurrent() {
        return current;
    }

    public int getLimit() {
        return limit;
    }

    public int getRows() {
        return rows;
    }

    public String getPath() {
        return path;
    }

    //获取当前页的起始行
    public int getOffset(){
        return (current - 1) * limit;
    }

    //获取总页数
    public int getTotal(){
        if(rows % limit == 0){
            return rows / limit;
        }else{
            return (rows / limit);
        }
    }

    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1:from;
    }
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total:to;

    }
}

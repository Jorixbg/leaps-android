package com.example.xcomputers.leaps.utils;

import com.example.networking.feed.trainer.Entity;

/**
 * Created by xComputers on 23/07/2017.
 */

public class EntityHolder {

    private Entity entity;

    private EntityHolder(){

    }

    private static EntityHolder instance;

    public static EntityHolder getInstance(){
        if(instance == null){
            instance = new EntityHolder();
        }
        return instance;
    }

    public static void clear(){
        instance = null;
    }

    public void setEntity(Entity entity){
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}

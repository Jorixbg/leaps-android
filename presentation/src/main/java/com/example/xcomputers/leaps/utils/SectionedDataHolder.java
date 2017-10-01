package com.example.xcomputers.leaps.utils;

import com.example.networking.feed.event.Event;
import com.example.networking.feed.event.RealEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xComputers on 14/06/2017.
 */

public class SectionedDataHolder {

    private List<String> sections;
    private List<List<RealEvent>> data;

    public SectionedDataHolder() {
        this.sections = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    public List<String> getSections() {
        return sections;
    }

    public void addSection(String sectionTitle, List<RealEvent> data) {

        if (sections.contains(sectionTitle)) {
            this.data.get(sections.indexOf(sectionTitle)).addAll(data);
        } else {
            sections.add(sectionTitle);
            this.data.add(data);
        }
    }

    public void addEvent(String sectionTitle, RealEvent event) {
        if (sections.contains(sectionTitle)) {
            if (!this.data.get(sections.indexOf(sectionTitle)).contains(event)) {
                this.data.get(sections.indexOf(sectionTitle)).add(event);
            }
        } else {
            sections.add(sectionTitle);
            List<RealEvent> list = new ArrayList<>();
            list.add(event);
            this.data.add(list);
        }
    }

    public boolean isEmpty(){
        return data.isEmpty();
    }

    public void addLoadingItem(){
        data.get(data.size() -1).add(null);
    }

    public void removeLoadingItem(){
        data.get(data.size() -1).remove(null);
    }

    public int getSectionCount() {
        return sections.size();
    }

    public int getItemsCount(int sectionIndex) {
        return data.size() == 0 ? 0 : data.get(sectionIndex).size();
    }

    public String getHeaderForSection(int section) {
        return sections.get(section);
    }

    public Event getItem(int sectionIndex, int relativePosition) {
        return data.get(sectionIndex).get(relativePosition);
    }

    public List<RealEvent> getItemsForSection(int sectionIndex){
        if(data.isEmpty()){
            return new ArrayList<>();
        }
        return data.get(sectionIndex);
    }
}

package club.leaps.presentation.homecalendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.ItemCoord;
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;
import com.bumptech.glide.Glide;
import club.leaps.networking.feed.event.Event;
import club.leaps.presentation.LeapsApplication;
import club.leaps.presentation.R;
import club.leaps.presentation.utils.SectionedDataHolder;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by xComputers on 18/06/2017.
 */

public class HomeCalendarAdapter extends SectionedRecyclerViewAdapter<SectionedViewHolder> {

    private SectionedDataHolder data;
    private OnHeaderClickListener headerClickListener;
    private OnItemClickListener itemListener;
    private boolean isLoading;

    private static final int VIEW_TYPE_LOADING = 123343;

    public HomeCalendarAdapter(SectionedDataHolder data) {

        this.data = data == null ? new SectionedDataHolder() : data;
    }

    public void setLoadingState(boolean isLoading){
        this.isLoading = isLoading;
        if(isLoading){
            data.addLoadingItem();
        }else{
            data.removeLoadingItem();
        }
    }

    public boolean getLoadState(){
        return isLoading;
    }

    public void setData(SectionedDataHolder holder){
        setLoadingState(false);
        this.data = holder;
        notifyDataSetChanged();
    }

    @Override
    public int getSectionCount() {

        return data.getSectionCount();
    }

    @Override
    public int getItemCount(int sectionIndex) {

        return data.getItemsCount(sectionIndex);
    }

    @Override
    public int getItemViewType(int section, int relativePosition, int absolutePosition) {
        if(isLoading && data.getItem(section, relativePosition) == null){
            return VIEW_TYPE_LOADING;
        }
        return super.getItemViewType(section, relativePosition, absolutePosition);
    }

    @Override
    public void onBindHeaderViewHolder(SectionedViewHolder holder, int section, boolean expanded) {
        CalHeaderViewHolder headerHolder = (CalHeaderViewHolder) holder;
        headerHolder.headerName.setText(data.getHeaderForSection(section));
    }

    @Override
    public void onBindViewHolder(SectionedViewHolder holder, int section, int relativePosition, int absolutePosition) {
        if(data.getItem(section, relativePosition) == null){
            return;
        }
        if(getItemViewType(section, relativePosition, absolutePosition) == VIEW_TYPE_LOADING){
            return;
        }
        CalItemViewHolder mainHolder = (CalItemViewHolder) holder;
        Event event = data.getItem(section, relativePosition);
        Glide.with(mainHolder.itemPic.getContext())
                .load(LeapsApplication.BASE_URL + File.separator + event.imageUrl())
                .placeholder(R.drawable.event_placeholder)
                .into(mainHolder.itemPic);

        mainHolder.itemTitle.setText(event.title());
        mainHolder.itemName.setText(event.ownerName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.timeFrom());
        mainHolder.itemDetails.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                + ", "
                + calendar.get(Calendar.HOUR)
                + ":"
                + String.format(Locale.getDefault(), "%02d",calendar.get(Calendar.MINUTE)));
    }

    @Override
    public void onBindFooterViewHolder(SectionedViewHolder holder, int section) {
        // Setup footer view, if footers are enabled (see the next section)
    }

    @Override
    public SectionedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_recycler_item, parent, false));
            case VIEW_TYPE_HEADER:
                return new CalHeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_feed_recycler_header, parent, false));
            default:
                return new CalItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_cal_recycler_item, parent, false));
        }
    }

    class CalHeaderViewHolder extends SectionedViewHolder implements View.OnClickListener {
        private TextView headerName;

        CalHeaderViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            headerName = (TextView) itemView.findViewById(R.id.home_feed_header_name);
            headerName.setAllCaps(false);
            itemView.findViewById(R.id.feed_header_lbl).setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            if(headerClickListener != null) {
                headerClickListener.onHeaderClicked(getRelativePosition().section());
            }
        }
    }

    class LoadingViewHolder extends SectionedViewHolder{

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    class CalItemViewHolder extends SectionedViewHolder
            implements View.OnClickListener {


        private ImageView itemPic;
        private TextView itemName;
        private TextView itemTitle;
        private TextView itemDetails;


        CalItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemPic = (ImageView) itemView.findViewById(R.id.cal_recycler_item_pic);
            itemName = (TextView) itemView.findViewById(R.id.cal_recycler_item_name);
            itemTitle = (TextView) itemView.findViewById(R.id.cal_recycler_item_title);
            itemDetails = (TextView) itemView.findViewById(R.id.cal_recycler_item_details);

        }

        @Override
        public void onClick(View view) {
            ItemCoord position = getRelativePosition();
            if(itemListener != null) {
                itemListener.onItemClicked(position.section(), position.relativePos());
            }
        }
    }

    public void registerItemClickListener(OnItemClickListener listener){
        this.itemListener = listener;
    }
    public void registerHeaderClickListener(OnHeaderClickListener listener){
        this.headerClickListener = listener;
    }

    public interface OnHeaderClickListener{
        void onHeaderClicked(int section);
    }
    public interface OnItemClickListener{
        void onItemClicked(int sectionIndex, int relativePosition);
    }
}
package com.homehub.dragan.myhomehub.UI;


import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homehub.dragan.myhomehub.R;

import java.util.List;

public class ComplexRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Object> items;

    private final int NEW = 0, IMAGE = 1, SWITCH = 2, SLIDER = 3;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ComplexRecyclerViewAdapter(List<Object> items) {
        this.items = items;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Boolean) {
            return NEW;
        } else if (items.get(position) instanceof String) {
            return IMAGE;
        } else if (items.get(position) instanceof SwitchBasedControl) {
            return SWITCH;
        } else if (items.get(position) instanceof SliderBasedControl) {
            return SLIDER;
        }
        return -1;
    }

    /**
     * This method creates different RecyclerView.ViewHolder objects based on the item view type.\
     *
     * @param viewGroup ViewGroup container for the item
     * @param viewType type of view to be inflated
     * @return viewHolder to be inflated
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case NEW:
                View v1 = inflater.inflate(R.layout.layout_addnewdevice, viewGroup, false);
                viewHolder = new AddNewDeviceViewHolder(v1);
                break;
            case IMAGE:
                View v2 = inflater.inflate(R.layout.layout_viewholder2, viewGroup, false);
                viewHolder = new ViewHolder2(v2);
                break;

            case SWITCH:
                View v3 = inflater.inflate(R.layout.switch_control_viewholder, viewGroup, false);
                viewHolder = new SwitchControlViewHolder(v3);
                break;
            case SLIDER:
                View v4 = inflater.inflate(R.layout.slider_control_viewholder, viewGroup, false);
                viewHolder = new SliderControlViewHolder(v4);
                break;

            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                viewHolder = new RecyclerView.ViewHolder(v) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
                break;
        }
        return viewHolder;
    }

    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param viewHolder The type of RecyclerView.ViewHolder to populate
     * @param position Item position in the viewgroup.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case NEW:
                AddNewDeviceViewHolder vh1 = (AddNewDeviceViewHolder) viewHolder;
                configureAddNewDeviceViewHolder(vh1, position);
                break;
            case IMAGE:
                ViewHolder2 vh2 = (ViewHolder2) viewHolder;
                configureViewHolder2(vh2);
                break;
            case SWITCH:
                SwitchControlViewHolder vh3 = (SwitchControlViewHolder) viewHolder;
                configureSwitchViewHolder(vh3, position);
                break;
            case SLIDER:
                SliderControlViewHolder vh4 = (SliderControlViewHolder) viewHolder;
                configureSliderViewHolder(vh4, position);
                break;
            default:
                //skip it
                break;
        }
    }



    private void configureAddNewDeviceViewHolder(AddNewDeviceViewHolder vh, int position) {
         //FloatingActionButton addbutton = (FloatingActionButton) items.get(position);
         //addbutton = vh.getAddButton();

    }


    private void configureViewHolder2(ViewHolder2 vh2) {
        vh2.getImageView().setImageResource(R.drawable.ic_launcher_foreground);
    }

    private void configureSwitchViewHolder(SwitchControlViewHolder vh3, int position) {
        SwitchBasedControl device = (SwitchBasedControl) items.get(position);
        if (device != null) {
            vh3.getLabel1().setText(device.getLinkedDeviceId());
            //vh3.getControl().setText(Boolean.toString(device.getSwitchState()));
        }

        boolean state = device.getSwitchState();

        vh3.setControl(state);
    }

    private void configureSliderViewHolder(final SliderControlViewHolder vh3, int position) {
        SliderBasedControl device = (SliderBasedControl) items.get(position);

        if (device != null) {
            vh3.getLabel1().setText(device.getLinkedDeviceId());
            //vh3.getSeekBar(device.getSlider());
            vh3.getValue().setText(Double.toString(device.getProgress()));// setCurrentValue(57);
            vh3.setCurrentValue(device.getProgress());
        }
    }

}
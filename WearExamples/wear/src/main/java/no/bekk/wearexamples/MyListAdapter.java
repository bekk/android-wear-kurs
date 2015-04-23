package no.bekk.wearexamples;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyListAdapter extends WearableListView.Adapter {

    private final List<Item> items;
    private final Context context;

    public MyListAdapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ItemView itemView = new ItemView(context);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked " , Toast.LENGTH_SHORT).show();
            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        Item item = items.get(position);
        ItemView view = (ItemView) holder.itemView;
        view.itemContentView.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends WearableListView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private final class ItemView extends FrameLayout implements WearableListView.OnCenterProximityListener {
        private static final int ANIMATION_DURATION_MS = 150;
        private static final float SHRINK_CIRCLE_RATIO = .80f;
        private static final float SHRINK_LABEL_ALPHA = .5f;
        private static final float EXPAND_LABEL_ALPHA = 1f;

        public final TextView itemContentView;
        public final CircledImageView imageView;
        private final ObjectAnimator expandCircleAnimator;
        private final ObjectAnimator expandLabelAnimator;
        private final AnimatorSet expandAnimator;

        private final float expandCircleRadius;
        private final float shrinkCircleRadius;
        private final ObjectAnimator shrinkCircleAnimator;
        private final ObjectAnimator shrinkLabelAnimator;
        private final AnimatorSet shrinkAnimator;

        public ItemView(Context context) {
            super(context);
            View.inflate(context, R.layout.todo_item_layout, this);
            this.itemContentView = (TextView) findViewById(R.id.text);
            this.imageView = (CircledImageView) findViewById(R.id.image);
            expandCircleRadius = imageView.getCircleRadius();
            shrinkCircleRadius = expandCircleRadius * SHRINK_CIRCLE_RATIO;

            shrinkCircleAnimator = ObjectAnimator.ofFloat(imageView, "circleRadius",
                    expandCircleRadius, shrinkCircleRadius);
            shrinkLabelAnimator = ObjectAnimator.ofFloat(itemContentView, "alpha",
                    EXPAND_LABEL_ALPHA, SHRINK_LABEL_ALPHA);
            shrinkAnimator = new AnimatorSet().setDuration(ANIMATION_DURATION_MS);
            shrinkAnimator.playTogether(shrinkCircleAnimator, shrinkLabelAnimator);

            expandCircleAnimator = ObjectAnimator.ofFloat(imageView, "circleRadius",
                    shrinkCircleRadius, expandCircleRadius);
            expandLabelAnimator = ObjectAnimator.ofFloat(itemContentView, "alpha",
                    SHRINK_LABEL_ALPHA, EXPAND_LABEL_ALPHA);
            expandAnimator = new AnimatorSet().setDuration(ANIMATION_DURATION_MS);
            expandAnimator.playTogether(expandCircleAnimator, expandLabelAnimator);
        }

        @Override
        public void onCenterPosition(boolean animate) {
            if (animate) {
                shrinkAnimator.cancel();
                if (!expandAnimator.isRunning()) {
                    expandCircleAnimator.setFloatValues(imageView.getCircleRadius(), expandCircleRadius);
                    expandLabelAnimator.setFloatValues(itemContentView.getAlpha(), EXPAND_LABEL_ALPHA);
                    expandAnimator.start();
                }
            } else {
                expandAnimator.cancel();
                imageView.setCircleRadius(expandCircleRadius);
                itemContentView.setAlpha(EXPAND_LABEL_ALPHA);
            }
        }

        @Override
        public void onNonCenterPosition(boolean animate) {
            if (animate) {
                expandAnimator.cancel();
                if (!shrinkAnimator.isRunning()) {
                    shrinkCircleAnimator.setFloatValues(imageView.getCircleRadius(), shrinkCircleRadius);
                    shrinkLabelAnimator.setFloatValues(itemContentView.getAlpha(), SHRINK_LABEL_ALPHA);
                    shrinkAnimator.start();
                }
            } else {
                shrinkAnimator.cancel();
                imageView.setCircleRadius(shrinkCircleRadius);
                itemContentView.setAlpha(SHRINK_LABEL_ALPHA);
            }
        }
    }
}

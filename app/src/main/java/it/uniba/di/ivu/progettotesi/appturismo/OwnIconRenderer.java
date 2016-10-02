package it.uniba.di.ivu.progettotesi.appturismo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.DrawableRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

public class OwnIconRenderer extends DefaultClusterRenderer<AppClusterItem> {

    private final IconGenerator mIconGenerator;
    private ShapeDrawable mColoredRectBackground;
    private SparseArray<BitmapDescriptor> mIcons = new SparseArray();
    private final float mDensity;
    private Context mContext;

    public OwnIconRenderer(Context context, GoogleMap map,
                             ClusterManager<AppClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.mContext = context;
        this.mDensity = context.getResources().getDisplayMetrics().density;
        this.mIconGenerator = new IconGenerator(context);
        this.mIconGenerator.setContentView(this.makeSquareTextView(context));
        this.mIconGenerator.setTextAppearance(
                com.google.maps.android.R.style.ClusterIcon_TextAppearance);
        this.mIconGenerator.setBackground(this.makeClusterBackground());
    }

    private SquareTextView makeSquareTextView(Context context) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(com.google.maps.android.R.id.text);
        int twelveDpi = (int)(12.0F * this.mDensity);
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
        return squareTextView;
    }

    private LayerDrawable makeClusterBackground() {
        // Outline color
        int clusterOutlineColor = mContext.getResources().getColor(R.color.md_indigo_500);

        this.mColoredRectBackground = new ShapeDrawable(new RectShape());
        ShapeDrawable outline = new ShapeDrawable(new RectShape());
        outline.getPaint().setColor(clusterOutlineColor);
        LayerDrawable background = new LayerDrawable(
                new Drawable[]{outline, this.mColoredRectBackground});
        int strokeWidth = (int)(this.mDensity * 3.0F);
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        return background;
    }

    @Override
    protected void onBeforeClusterItemRendered(AppClusterItem item,
                                               MarkerOptions markerOptions) {
        markerOptions.icon(item.getIcon());
       // markerOptions.title(item.getPosition().toString());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onClusterItemRendered(AppClusterItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }
    @Override
    protected boolean shouldRenderAsCluster(Cluster<AppClusterItem> cluster) {
        //start clustering if at least 2 items overlap
        return cluster.getSize() > 2;
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<AppClusterItem> cluster, MarkerOptions markerOptions) {

        // Main color
        int clusterColor = mContext.getResources().getColor(R.color.md_indigo_700);

        int bucket = this.getBucket(cluster);
        BitmapDescriptor descriptor = this.mIcons.get(bucket);
        if(descriptor == null) {
            this.mColoredRectBackground.getPaint().setColor(clusterColor);
            descriptor = BitmapDescriptorFactory.fromBitmap(
                    this.mIconGenerator.makeIcon(this.getClusterText(bucket)));
            this.mIcons.put(bucket, descriptor);
        }

        markerOptions.icon(descriptor);
}
}

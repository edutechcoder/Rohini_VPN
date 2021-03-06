package com.edutechdeveloper.rohinivpn.vpn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.facebook.ads.Ad;
//import com.facebook.ads.AdError;
//import com.facebook.ads.AdIconView;
//import com.facebook.ads.AdOptionsView;
//import com.facebook.ads.MediaView;
//import com.facebook.ads.NativeAdLayout;
//import com.facebook.ads.NativeAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.northghost.hydraclient.R;

import java.util.ArrayList;
import java.util.List;

public class FBNativeAdapter extends RecyclerViewAdapterWrapper {

    private static final int TYPE_FB_NATIVE_ADS = 900;
    private static final int DEFAULT_AD_ITEM_INTERVAL = 10;

    private final Param mParam;

    private FBNativeAdapter(Param param) {
        super(param.adapter);
        this.mParam = param;

        assertConfig();
        setSpanAds();
    }

    private void assertConfig() {
        if (mParam.gridLayoutManager != null) {
            //if user set span ads
            int nCol = mParam.gridLayoutManager.getSpanCount();
            if (mParam.adItemInterval % nCol != 0) {
                throw new IllegalArgumentException(String.format("The adItemInterval (%d) is not divisible by number of columns in GridLayoutManager (%d)", mParam.adItemInterval, nCol));
            }
        }
    }

    private int convertAdPosition2OrgPosition(int position) {
        return position - (position + 1) / (mParam.adItemInterval + 1);
    }

    @Override
    public int getItemCount() {
        int realCount = super.getItemCount();
        return realCount + realCount / mParam.adItemInterval;
    }

    @Override
    public int getItemViewType(int position) {
        if (isAdPosition(position)) {
            return TYPE_FB_NATIVE_ADS;
        }
        return super.getItemViewType(convertAdPosition2OrgPosition(position));
    }

    private boolean isAdPosition(int position) {
        return (position + 1) % (mParam.adItemInterval + 1) == 0;
    }

    private void onBindAdViewHolder(final RecyclerView.ViewHolder holder) {
        final AdViewHolder adHolder = (AdViewHolder) holder;
        if (mParam.forceReloadAdOnBind || !adHolder.loaded) {

            final com.facebook.ads.NativeBannerAd nativeBannerAd = new com.facebook.ads.NativeBannerAd(adHolder.getContext(), mParam.facebookPlacementId);
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {

                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    adHolder.nativeAdContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (ad != nativeBannerAd) {
                        return;
                    }
                    adHolder.nativeAdContainer.setVisibility(View.VISIBLE);


                    // Set the Text.
                    adHolder.nativeSponserLable.setText(nativeBannerAd.getAdvertiserName());
                    adHolder.nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
                    adHolder.nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
                    adHolder.nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());


                    // Add the AdChoices icon
                    //AdOptionsView adChoicesView = new AdOptionsView(adHolder.getContext(), nativeAdListener, null);
                    AdOptionsView adOptionsView = new AdOptionsView(adHolder.getContext(), nativeBannerAd, null);
                    adHolder.adChoicesContainer.removeAllViews();
                    adHolder.adChoicesContainer.addView(adOptionsView);

                    List<View> list = new ArrayList<>();
                    list.add(adHolder.nativeAdTitle);
                    list.add(adHolder.nativeAdCallToAction);

                    nativeBannerAd.registerViewForInteraction(adHolder.nativeAdContainer, adHolder.nativeAdIconView, list);

                    adHolder.loaded = true;
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig()
                    .withAdListener(nativeAdListener)
                    .build());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FB_NATIVE_ADS) {
            onBindAdViewHolder(holder);
        } else {
            super.onBindViewHolder(holder, convertAdPosition2OrgPosition(position));
        }
    }

    private RecyclerView.ViewHolder onCreateAdViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View adLayoutOutline = inflater
                .inflate(mParam.itemContainerLayoutRes, parent, false);
        ViewGroup vg = adLayoutOutline.findViewById(mParam.itemContainerId);

        LinearLayout adLayoutContent = (LinearLayout) inflater
                .inflate(R.layout.item_facebook_native_ad, parent, false);
        vg.addView(adLayoutContent);
        return new AdViewHolder(adLayoutOutline);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FB_NATIVE_ADS) {
            return onCreateAdViewHolder(parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    private void setSpanAds() {
        if (mParam.gridLayoutManager == null) {
            return;
        }
        final GridLayoutManager.SpanSizeLookup spl = mParam.gridLayoutManager.getSpanSizeLookup();
        mParam.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (isAdPosition(position)) {
                    return spl.getSpanSize(position);
                }
                return 1;
            }
        });
    }

    private static class Param {
        String facebookPlacementId;
        RecyclerView.Adapter adapter;
        int adItemInterval;
        boolean forceReloadAdOnBind;

        @LayoutRes
        int itemContainerLayoutRes;

        @IdRes
        int itemContainerId;

        GridLayoutManager gridLayoutManager;
    }

    public static class Builder {
        private final Param mParam;

        private Builder(Param param) {
            mParam = param;
        }

        public static Builder with(String placementId, RecyclerView.Adapter wrapped) {
            Param param = new Param();
            param.facebookPlacementId = placementId;
            param.adapter = wrapped;

            //default value
            param.adItemInterval = DEFAULT_AD_ITEM_INTERVAL;
            param.itemContainerLayoutRes = R.layout.item_facebook_native_ad_outline;
            param.itemContainerId = R.id.native_banner_ad_container;
            param.forceReloadAdOnBind = true;
            return new Builder(param);
        }

        public Builder adItemInterval(int interval) {
            mParam.adItemInterval = interval;
            return this;
        }

        public Builder adLayout(@LayoutRes int layoutContainerRes, @IdRes int itemContainerId) {
            mParam.itemContainerLayoutRes = layoutContainerRes;
            mParam.itemContainerId = itemContainerId;
            return this;
        }

        public FBNativeAdapter build() {
            return new FBNativeAdapter(mParam);
        }

        public Builder enableSpanRow(GridLayoutManager layoutManager) {
            mParam.gridLayoutManager = layoutManager;
            return this;
        }

        public Builder forceReloadAdOnBind(boolean forced) {
            mParam.forceReloadAdOnBind = forced;
            return this;
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {
        MediaView nativeAdIconView;
        TextView nativeAdTitle;
        MediaView nativeAdMedia;
        TextView nativeAdSocialContext;
        TextView nativeAdBody;
        Button nativeAdCallToAction;
        RelativeLayout adChoicesContainer;
        NativeAdLayout nativeAdContainer;
        TextView nativeSponserLable;
        boolean loaded;

        AdViewHolder(View view) {
            super(view);
            nativeAdContainer = view.findViewById(R.id.native_banner_ad_container);
            nativeAdIconView = view.findViewById(R.id.native_icon_view);
            nativeAdTitle = view.findViewById(R.id.native_ad_title);
            nativeAdSocialContext = view.findViewById(R.id.native_ad_social_context);
            nativeAdCallToAction = view.findViewById(R.id.native_ad_call_to_action);
            adChoicesContainer = view.findViewById(R.id.ad_choices_container);
            nativeSponserLable = view.findViewById(R.id.native_ad_sponsored_label);
            loaded = false;
        }

        Context getContext() {
            return nativeAdContainer.getContext();
        }
    }
}

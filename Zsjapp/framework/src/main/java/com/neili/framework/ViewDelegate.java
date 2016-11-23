package com.neili.framework;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class ViewDelegate implements IView {
    protected final SparseArray<View> mViews = new SparseArray<View>();
    protected View rootView;
    private Unbinder unbinder;

    @Override
    public void create(LayoutInflater inflater, ViewGroup container,
                       Bundle savedInstanceState) {
        int layoutId = getRootLayoutId();
        rootView = inflater.inflate(layoutId, container, false);
    }

    @Override
    public int getOptionsMenuId() {
        return 0;
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public abstract int getRootLayoutId();

    @Override
    public void initWidget() {
        unbinder=ButterKnife.bind(this, getActivity());
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T bindView(int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) rootView.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T get(int id) {
        return (T) bindView(id);
    }

    public void setOnClickListener(View.OnClickListener listener, int... ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            get(id).setOnClickListener(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Activity> T getActivity() {
        return (T) rootView.getContext();
    }

    public void deinitWidget() {
        unbinder.unbind();
    }
}

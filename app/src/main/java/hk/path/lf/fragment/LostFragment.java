package hk.path.lf.fragment;
/**
 * lost fragment
 */

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hk.path.lf.R;
import hk.path.lf.adapt.LostRecyclerViewAdapter;
import hk.path.lf.entities.API_GetLost;
import hk.path.lf.entities.API_GetLost_Ret;
import hk.path.lf.entities.API_Return;
import hk.path.lf.entities.LostItem;
import hk.path.lf.net.api;
import hk.path.lf.view.LoadMoreRecyclerView;


public class LostFragment extends Fragment {
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    private List<LostItem> mDatas;
    private LoadMoreRecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LostRecyclerViewAdapter lostRecyclerViewAdapter;
    private int count = 1;

    public LostFragment() {
    }

    @SuppressWarnings("unused")
    public static LostFragment newInstance(int columnCount) {
        LostFragment fragment = new LostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        findView(view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                api.Request(new API_GetLost(1), new API_Return<API_GetLost_Ret>() {
                    @Override
                    public void ret(int Code, API_GetLost_Ret ret) {
                        if (Code == 0) {
                            LostItem[] mDatas = ret.getLostItems();
                            swipeRefreshLayout.setRefreshing(false);
                            lostRecyclerViewAdapter.setDatas(mDatas);
                            if (ret.getTotalRows() <= 10) {
                                recyclerView.setAutoLoadMoreEnable(false);
                            } else {
                                recyclerView.setAutoLoadMoreEnable(true);
                            }
                            recyclerView.scrollToPosition(0);
                            recyclerView.setAdapter(lostRecyclerViewAdapter);
                            //lostRecyclerViewAdapter.noti
                        } else {
                            System.out.println("error:" + Code);
                        }
                    }
                }, LostFragment.this.getActivity());

            }
        });
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        lostRecyclerViewAdapter = new LostRecyclerViewAdapter(this.getActivity(), mDatas);
        recyclerView.setAdapter(lostRecyclerViewAdapter);
        recyclerView.setAutoLoadMoreEnable(true);
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count++;
                        api.Request(new API_GetLost(count), new API_Return<API_GetLost_Ret>() {
                            @Override
                            public void ret(int Code, API_GetLost_Ret ret) {
                                if (Code == 0) {
                                    LostItem[] mDatas = ret.getLostItems();
                                    recyclerView.setAutoLoadMoreEnable(true);
                                    lostRecyclerViewAdapter.addDatas(mDatas);
                                    recyclerView.notifyMoreFinish(ret.getTotalRows() > count * 10);
                                } else {
                                    System.out.println("error:" + Code);
                                }
                            }
                        }, LostFragment.this.getActivity());

                    }
                }, 1000);
            }
        });
        lostRecyclerViewAdapter.notifyDataSetChanged();

        initData();
        return view;
    }

    private void initData() {
        api.Request(new API_GetLost(1), new API_Return<API_GetLost_Ret>() {
            @Override
            public void ret(int Code, API_GetLost_Ret ret) {
                if (Code == 0) {
                    LostItem[] mDatas = ret.getLostItems();
                    swipeRefreshLayout.setRefreshing(false);
                    lostRecyclerViewAdapter.setDatas(mDatas);
                    if (ret.getTotalRows() <= 10) {
                        recyclerView.setAutoLoadMoreEnable(false);
                    } else {
                        recyclerView.setAutoLoadMoreEnable(true);
                    }
                    lostRecyclerViewAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(0);
                } else {
                    System.out.println("error:" + Code);
                }
            }
        }, LostFragment.this.getActivity());
    }

    private void findView(View view) {
        recyclerView = (LoadMoreRecyclerView) view.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

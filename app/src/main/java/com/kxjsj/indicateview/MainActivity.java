package com.kxjsj.indicateview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.security.spec.PSSParameterSpec;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      final   RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new StaggeredLayoutManager().setCount(3));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return SimpleViewHolder.createViewHolder(parent,R.layout.tt);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                SimpleViewHolder holde= (SimpleViewHolder) holder;
                holde.setText(R.id.tv,position+"");
                System.out.println(position+"--------------------");
            }

            @Override
            public int getItemCount() {
                return 120;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println(recyclerView.getMeasuredHeight()+"-----qqq");
            }
        },100);

    }
}

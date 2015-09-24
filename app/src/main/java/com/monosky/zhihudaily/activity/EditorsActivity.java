package com.monosky.zhihudaily.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.activity.adapter.EditorsAdapter;
import com.monosky.zhihudaily.module.EditorData;

import java.util.ArrayList;

/**
 * 主编列表activity
 */
public class EditorsActivity extends BaseActivity {

    private EditorsActivity mInstance;
    private Toolbar mToolbar;
    private RecyclerView mEditorRecyclerView;
    private EditorsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editors);
        mInstance = EditorsActivity.this;

        getViews();
        setViews();

        ArrayList<EditorData> editorDatas = (ArrayList<EditorData>) getIntent().getSerializableExtra("editors");
        mAdapter = new EditorsAdapter(mInstance, editorDatas);
        mEditorRecyclerView.setAdapter(mAdapter);

    }

    private void getViews() {
        mToolbar = (Toolbar) findViewById(R.id.editor_tool_bar);
        mEditorRecyclerView = (RecyclerView) findViewById(R.id.editor_recycler_view);
    }

    private void setViews() {
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setTitle(getResources().getString(R.string.theme_editor));
        mToolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        setSupportActionBar(mToolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mInstance);
        mEditorRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

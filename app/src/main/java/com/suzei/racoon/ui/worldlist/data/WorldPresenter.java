package com.suzei.racoon.ui.worldlist.data;

import com.google.firebase.database.DatabaseError;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.worldlist.adapter.WorldAdapter;

public class WorldPresenter implements Contract.Listener<WorldAdapter> {

    private Contract.AdapterView<WorldAdapter> adapterView;
    private WorldInteractor worldInteractor;

    public WorldPresenter(Contract.AdapterView<WorldAdapter> adapterView) {
        this.adapterView = adapterView;
        worldInteractor = new WorldInteractor(this);
    }

    public void start() {
        worldInteractor.performFirebaseDatabaseLoad();
    }

    @Override
    public void onLoadSuccess(WorldAdapter data) {
        adapterView.setAdapter(data);
    }

    @Override
    public void onLoadFailed(DatabaseError error) {

    }
}

package rpfm.cursoandroid.coml.rpfm;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Robson Cabral on 08/04/2018.
 */
public class Tab2Comentarios extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2comentarios, container, false);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

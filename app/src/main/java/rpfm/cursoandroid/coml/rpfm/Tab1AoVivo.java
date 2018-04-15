package rpfm.cursoandroid.coml.rpfm;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import java.io.IOException;

/**
 * Created by Robson Cabral on 08/04/2018.
 */
public class Tab1AoVivo extends Fragment {

    private Boolean estadoInicial = false;
    private Boolean playPause = true;
    private Boolean estadoImg = true;
    private ImageView btnTocar;
    private ImageView imgStream;
    private MediaPlayer mediaPlayer;
    private ProgressDialog progressDialog;
    private Boolean prepared;
    private String stream = "http://hts01.painelstream.net:8364";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1aovivo, container, false);

        imgStream = (ImageView) rootView.findViewById(R.id.imgStreamId);
        btnTocar = (ImageView)  rootView.findViewById(R.id.btnExecutarId);
        btnTocar.setImageResource(R.drawable.play);
        imgStream.setVisibility(imgStream.INVISIBLE);

        return rootView;
    }

    private void streamPlay() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(getActivity());

        btnTocar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estadoInicial) {
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    estadoInicial = false;
                    estadoImg = true;
                } else {
                    if(playPause) {
                        new Player().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,stream);
                    } else {
                        if(!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                        }
                    }
                    estadoInicial = true;
                    playPause = false;
                    estadoImg = false;
                }
                checkIsPlaying();
            }
        });
        checkIsPlaying();
    }

    private void checkIsPlaying(){
        if(estadoImg) {
            btnTocar.setImageResource(R.drawable.play);
            imgStream.setImageResource(R.drawable.streaming_parado);
        } else {
            Glide.with(getActivity()).load(R.drawable.streaming).asGif().into(imgStream);
            btnTocar.setImageResource(R.drawable.stop);
        }
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        estadoInicial = false;
                        playPause = true;
                        btnTocar.setImageResource(R.drawable.play);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;

            } catch (IOException e) {
                e.printStackTrace();
                prepared = false;
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            btnTocar.setImageResource(R.drawable.stop);
            mediaPlayer.start();
            imgStream.setVisibility(imgStream.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Glide.with(getActivity()).load(R.drawable.carregando).asGif().into(btnTocar);
            progressDialog.setMessage("Carregando Streaming...");
            progressDialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(estadoInicial) {
            //mediaPlayer.start();
            checkIsPlaying();
        }
        streamPlay();
    }

    @Override
    public void onDestroy() {
        if(prepared) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}

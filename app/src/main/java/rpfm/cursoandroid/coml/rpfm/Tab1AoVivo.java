package rpfm.cursoandroid.coml.rpfm;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import java.io.IOException;

/**
 * Created by Robson Cabral on 08/04/2018.
 */
public class Tab1AoVivo extends Fragment {

    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private Boolean estadoInicial = false;
    private Boolean playPause = true;
    private Boolean estadoImg = true;
    private ImageView btnTocar;
    private ImageView imgStream;
    private MediaPlayer mediaPlayer;
    private Boolean prepared;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1aovivo, container, false);

        volumeSeekbar = (SeekBar) rootView.findViewById(R.id.seekBarVolumeId);
        imgStream = (ImageView) rootView.findViewById(R.id.imgStreamId);
        btnTocar = (ImageView)  rootView.findViewById(R.id.btnExecutarId);
        btnTocar.setImageResource(R.drawable.play);
        imgStream.setVisibility(imgStream.INVISIBLE);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        controleVolume();

        return rootView;
    }

    private void controleVolume()
    {
        try
        {

            audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void notificacao() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setSmallIcon( R.drawable.icon_notificacao )
                .setContentTitle( "RPfm 105.3" )
                .setContentText( "Ao Vivo" )
                .setAutoCancel( false );
        int id = 1;
        NotificationManager notifyManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.notify( id, builder.build() );
    }

    private void streamPlay() {
        //progressDialog = new ProgressDialog(getActivity());

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
                        new Player().execute("http://hts01.kshost.com.br:8364/live");
                    } else {
                        if(!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                        }
                    }
                    notificacao();
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
        private ProgressDialog progressDialog;
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        //Stub de método gerado automaticamente
                        estadoInicial = false;
                        playPause = true;
                        btnTocar.setImageResource(R.drawable.play);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;

            } catch (IllegalArgumentException e) {
                //Bloco catch gerado automaticamente
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                //Bloco catch gerado automaticamente
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                //Bloco catch gerado automaticamente
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                //Bloco catch gerado automaticamente
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            Log.d("Prepared", "//" + aBoolean);
            btnTocar.setImageResource(R.drawable.stop);
            mediaPlayer.start();
            playPause = false;
            imgStream.setVisibility(imgStream.VISIBLE);
        }

        public Player() {
            progressDialog = new ProgressDialog(getActivity());
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
            mediaPlayer.start();
            checkIsPlaying();
        } else {
            streamPlay();
        }
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

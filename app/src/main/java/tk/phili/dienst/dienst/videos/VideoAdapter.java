package tk.phili.dienst.dienst.videos;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.roundcornerprogressbar.indeterminate.IndeterminateCenteredRoundCornerProgressBar;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import tk.phili.dienst.dienst.R;

/**
 * Created by fipsi on 04.03.2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    public List<Integer> id;
    public List<String> title;
    public List<String> length;
    public List<String> mb;
    public List<String> url;
    public List<Boolean> isDownloaded;
    public HashMap<Integer, Drawable> drawables = new HashMap<Integer, Drawable>();
    private VideoActivity context;

    public HashMap<Long, Integer> pendingDownload = new HashMap<Long, Integer>();

    public VideoAdapter(VideoActivity context, List<Integer> id, List<String> title, List<String> length, List<String> mb, List<String> url, List<Boolean> isDownloaded) {
        this.id = id;
        this.title = title;
        this.length = length;
        this.mb = mb;
        this.url = url;
        this.isDownloaded = isDownloaded;
        this.context = context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final SharedPreferences sp = context.getSharedPreferences("MainActivity", context.MODE_PRIVATE);

        final int vidId = id.get(position);
        final String vidTitle = title.get(position);
        final String vidLength = length.get(position);
        final String vidMb = mb.get(position);
        final String vidURL = url.get(position);
        final boolean vidDownloaded = isDownloaded.get(position);
        Log.d("Dienstapp-URL", vidURL);

        holder.title.setText(vidTitle);
        holder.time.setText(vidLength);
        holder.mb.setText(vidMb);

        holder.imageGradient.setVisibility(View.INVISIBLE);
        if(vidDownloaded){
            holder.downloadedImg.setVisibility(View.VISIBLE);
            holder.notDownloadedImg.setVisibility(View.INVISIBLE);
            holder.mb.setVisibility(View.INVISIBLE);
            if(!drawables.containsKey(position)) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath()+"/MINISTRY/" + vidTitle.replace("?", "") + ".mp4", MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        final Drawable d = new BitmapDrawable(context.getResources(), bMap);
                        drawables.put(position, d);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.imageGradient.setVisibility(View.VISIBLE);
                                holder.image.setImageDrawable(d);
                            }
                        });
                        return null;
                    }
                }.execute();
            }else{
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.imageGradient.setVisibility(View.VISIBLE);
                        holder.image.setImageDrawable(drawables.get(position));
                    }
                });
            }
        }else{
            holder.downloadedImg.setVisibility(View.INVISIBLE);
            holder.notDownloadedImg.setVisibility(View.VISIBLE);
            holder.mb.setVisibility(View.VISIBLE);
            holder.image.setImageDrawable(null);

            if(pendingDownload.containsValue(vidId)){
                holder.downloadProgressBarIndeterminate.setVisibility(View.VISIBLE);
            }else{
                holder.downloadProgressBarIndeterminate.setVisibility(View.INVISIBLE);
            }
        }




        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vidDownloaded){
                    File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath() , "MINISTRY" + "/" + vidTitle.replace("?", "") + ".mp4");
                    Uri contentUri = FileProvider.getUriForFile(context, "tk.phili.dienst.dienst.fileprovider", file);
                    if(file.exists()) {
                        openFile(context, contentUri);
                    }else{
                        Toast.makeText(context, context.getString(R.string.videonew_lostfile), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.download_sure));
                    builder.setMessage(context.getString(R.string.download_text).replace("%a", vidTitle));

                    String positiveText = context.getString(R.string.download_ok);
                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                        long a = doDownload(vidTitle, vidURL);
                        holder.downloadProgressBarIndeterminate.setVisibility(View.VISIBLE);
                        pendingDownload.put(a, vidId);
                    });

                    String negativeText = context.getString(R.string.download_cancel);
                    builder.setNegativeButton(negativeText, null);

                    try {
                        AlertDialog dialog = builder.create();
                        // display dialog
                        dialog.show();
                    } catch (Exception e) {
                    }
                }
            }
        });

        holder.mainView.setOnLongClickListener(view -> {
            showDeleteDialog(vidTitle);
            return false;
        });







    }

    @Override public int getItemCount() {
        return id.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public ImageView imageGradient;
        public TextView title;
        public TextView time;
        public TextView mb;

        public ImageView notDownloadedImg;
        public ImageView downloadedImg;
        public View mainView;

        public IndeterminateCenteredRoundCornerProgressBar downloadProgressBarIndeterminate;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            title = itemView.findViewById(R.id.txt);
            image = itemView.findViewById(R.id.listpreviewimage);
            imageGradient = itemView.findViewById(R.id.listpreviewimagegradient);
            time = itemView.findViewById(R.id.time);
            mb = itemView.findViewById(R.id.mb);

            notDownloadedImg = itemView.findViewById(R.id.imageView6);
            downloadedImg = itemView.findViewById(R.id.imageView8);

            downloadProgressBarIndeterminate = itemView.findViewById(R.id.progress_download_indeterminate);
        }
    }


    public long doDownload(String videoname, String url){
        try {
            //DOWNLOAD
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(context.getString(R.string.splash_app_name));
            request.setTitle(videoname);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, "MINISTRY/"+ videoname.replace("?", "") + ".mp4");
            request.setVisibleInDownloadsUi(true);
            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            return manager.enqueue(request);

        }catch(Exception ex){
            Toast.makeText(context, context.getString(R.string.video_exception), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        return -1;
    }

    public static void openFile(Context context, Uri contentUri) {
        Log.d("VideoAdapter", contentUri.toString());
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
            intent.setDataAndType(contentUri, "video/mp4");
            intent.setClipData(ClipData.newRawUri("", contentUri));
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            context.startActivity(intent);
        }catch(Exception e){
            new AlertDialog.Builder(context)
                    .setTitle(R.string.error)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .setMessage(R.string.video_open_error)
                    .show();
        }
    }

    public void showDeleteDialog(final String vidname) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.delete_sure));
        builder.setMessage(context.getString(R.string.delete_text).replace("%a", vidname));

        String positiveText = context.getString(R.string.delete_ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath()+"/"+"MINISTRY" +"/"+ vidname.replace("?", "")+".mp4");
                        file.delete();
                        context.refreshList();
                    }
                });

        String negativeText = context.getString(R.string.delete_cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

    }


}

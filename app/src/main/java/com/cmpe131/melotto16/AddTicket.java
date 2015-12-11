package com.cmpe131.melotto16;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import checklotto.Lottery;
import control.MyApplication;
import control.Storager;

public class AddTicket extends MyActivity {

    private static final String TAG = "AddTicket";
    private TextView downloadResults;
    private TextView photosUploaded;
    private ArrayList<String> results;
    private ArrayAdapter<String> adapter;

    public static final String PHOTOFOLDER = "App5_Photos";
    public static final String FOLDERNAME = "App5";
    public static final String FILENAME = "App5_data.txt";

    private String folderId;
    private String fileId;

    private String driveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ticket);

        MyApplication app = (MyApplication)getApplication();
        downloadResults = (TextView)findViewById(R.id.txt_downloadResults);
        downloadResults.setVisibility(View.INVISIBLE);
        photosUploaded = (TextView)findViewById(R.id.txt_photoCount);
        photosUploaded.setText("Photos (" + app.getInformation().currentTicket.imageLink.size() + ")");

        ListView listView = (ListView) findViewById(R.id.listView);
        results = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        listView.setAdapter(adapter);

        String drawNumber = app.getInformation().currentTicket.drawNumber + "";
        int draws = app.getInformation().currentTicket.draws;
        for (int i = 0; i < draws; i++) {
            String[] args = new String[2];
            args[0] = drawNumber;
            args[1] = i + ""; //start position at 0
            startMyTask(new CheckWonTask(), args);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        MyApplication app = (MyApplication)getApplication();
        if (app.getInformation().photoFolderId == null)
            startMyTask(new getPhotoFolderAsyncTask(AddTicket.this), new String[0]);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    void startMyTask(AsyncTask asyncTask, String[] params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

    class CheckWonTask extends AsyncTask<String, Integer, String[]> {
        protected void onPreExecute()
        {
            downloadResults.setVisibility(View.VISIBLE);
        }
        protected String[] doInBackground(String... message) {
            //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            MyApplication app = (MyApplication)getApplication();
            String lottoType = app.getInformation().currentTicket.ticketType;
            Lottery lotto = app.getInformation().getLotto();
            int drawPosition = Integer.parseInt(message[1]); //starts at 0
            int drawNumber = Integer.parseInt(message[0]);
            if (drawNumber > 0)
                drawNumber += drawPosition;
            String[] output;
            try {
                int[] args;
                if (lottoType.equals("POWERBALL") || lottoType.equals("MEGAMillions") || lottoType.equals("SuperLottoPlus"))
                {
                    args = new int[7];
                    args[0] = drawNumber;
                    for (int i = 1; i <= 5; i++)
                    {
                        args[i] = app.getInformation().currentTicket.numbers[drawPosition].get(i-1);
                    }
                    args[6] = app.getInformation().currentTicket.specialNumber[drawPosition];
                }
                else if (lottoType.equals("Fantasy5"))
                {
                    args = new int[6];
                    args[0] = drawNumber;
                    for (int i = 1; i <= 5; i++)
                    {
                        args[i] = app.getInformation().currentTicket.numbers[drawPosition].get(i-1);
                    }
                }
                else if (lottoType.equals("Daily4"))
                {
                    args = new int[6];
                    args[0] = drawNumber;
                    for (int i = 1; i <= 4; i++)
                    {
                        args[i] = app.getInformation().currentTicket.numbers[drawPosition].get(i-1);
                    }
                    args[5] = app.getInformation().currentTicket.playType;
                }
                else if (lottoType.equals("Daily3"))
                {
                    args = new int[5];
                    args[0] = drawNumber;
                    for (int i = 1; i <= 3; i++)
                    {
                        args[i] = app.getInformation().currentTicket.numbers[drawPosition].get(i-1);
                    }
                    args[4] = app.getInformation().currentTicket.specialNumber[drawPosition];
                }
                else
                    throw new Exception();

                if (lotto.wasDrawn(drawNumber)) {
                    output = lotto.CheckIfWon(args);
                    app.getInformation().currentTicket.amountWon[drawPosition] = output[0];
                }
                else
                {
                    output = new String[3];
                    output[0] = "Not yet drawn";
                    output[1] = "";
                    output[2] = "";
                }
            }
            catch (UnknownHostException e) {
                output = new String[3];
                output[0] = "Cannot reach Host";
                output[1] = "";
                output[2] = "";
            }
            catch (IndexOutOfBoundsException e) {
                output = new String[3];
                output[0] = "Invalid Input";
                output[1] = "";
                output[2] = "";
            }
            catch (IllegalArgumentException e) {
                output = new String[3];
                output[0] = "Invalid Input";
                output[1] = "";
                output[2] = "";
            }
            catch (MalformedURLException e) {
                output = new String[3];
                output[0] = "Malformed URL";
                output[1] = "";
                output[2] = "";
            }
            catch (IOException e) {
                output = new String[3];
                output[0] = "Reading text failed";
                output[1] = "";
                output[2] = "";
            }
            catch (Exception e) {
                output = new String[3];
                output[0] = "Failed to read from web";
                output[1] = "";
                output[2] = "";
            }

            output[2] = drawNumber + "";
            return output;
        }

        protected void onPostExecute(String[] result) {
            downloadResults.setVisibility(View.INVISIBLE);
            StringBuilder sb = new StringBuilder();
            if (Integer.parseInt(result[2]) < 1)
            {
                result[2] = "N/A";
                result[1] = "";
                result[0] = "Draw Date not in Database";
            }
            sb.append("Draw Number: ");
            sb.append(result[2]);
            sb.append("\n");
            if (!result[1].equals("")) {
                sb.append(result[1]);
                sb.append("\nPrize Won: ");
            }
            sb.append(result[0]);
            results.add(sb.toString());
            Collections.sort(results);
            adapter.notifyDataSetChanged();
        }
    }

    public void takePhoto (View view) {
        MyApplication app = (MyApplication)getApplication();
        if (app.getInformation().photoFolderId != null) {
            showMessage("Taking photo");
            Intent intent = new Intent(AddTicket.this, AddPhoto.class);
            startActivityForResult(intent, 2);// Activity is started with requestCode 2
        }
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            Toast.makeText(AddTicket.this, "Photo Added", Toast.LENGTH_SHORT).show();
            MyApplication app = (MyApplication)getApplication();
            photosUploaded.setText("Photos (" + app.getInformation().currentTicket.imageLink.size() + ")");
        }
    }

    final private class getPhotoFolderAsyncTask
            extends ApiClientAsyncTask<String, Boolean, String> {

        public getPhotoFolderAsyncTask(Context context) {
            super(context);
        }
        boolean isFound;

        @Override
        protected String doInBackgroundConnected(String... params) {
            MyApplication app = (MyApplication)getApplication();

            Query query = new Query.Builder()
                    .addFilter(Filters.and(Filters.eq(
                                    SearchableField.TITLE, PHOTOFOLDER),
                            Filters.eq(SearchableField.TRASHED, false)))
                    .build();

            DriveApi.MetadataBufferResult result = Drive.DriveApi.query(mGoogleApiClient, query).await();
            isFound = false;
            if (!result.getStatus().isSuccess()) {
                showMessage("Cannot create folder in the root.");
            } else {
                showMessage("Found metadata:");
                for (Metadata m : result.getMetadataBuffer()) {
                    if (m.getTitle().equals(PHOTOFOLDER)) {
                        showMessage(m.getTitle());
                        app.getInformation().photoFolderId = m.getDriveId().encodeToString();
                        showMessage("Folder exists");
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    showMessage("Folder not found; creating it.");
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(PHOTOFOLDER)
                            .build();

                    Drive.DriveApi.getRootFolder(mGoogleApiClient)
                            .createFolder(mGoogleApiClient, changeSet).await();

                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the folder");
                    } else {
                        DriveApi.MetadataBufferResult result2 = Drive.DriveApi.query(mGoogleApiClient, query).await();

                        if (!result2.getStatus().isSuccess()) {
                            showMessage("Cannot create folder in the root.");
                        } else {
                            showMessage("Created a folder");
                            for (Metadata m : result2.getMetadataBuffer()) {
                                showMessage(m.getTitle()+" "+PHOTOFOLDER);
                                if (m.getTitle().equals(PHOTOFOLDER)) {
                                    showMessage("Folder exists");
                                    app.getInformation().photoFolderId = m.getDriveId().encodeToString();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return new String();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showMessage("Found photo folder");
        }
    }

    public void showMessage(String message) {
        Log.i(TAG, message);
        //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void finish (View view) {
        downloadResults.setText("Saving Ticket ...");
        downloadResults.setVisibility(View.VISIBLE);
        new CheckIfDataExistsAsyncTask(AddTicket.this).execute("");
    }

    final private class CheckIfDataExistsAsyncTask
            extends ApiClientAsyncTask<String, Boolean, String> {

        public CheckIfDataExistsAsyncTask(Context context) {
            super(context);
        }
        boolean isFound;

        @Override
        protected String doInBackgroundConnected(String... params) {
            Query query = new Query.Builder()
                    .addFilter(Filters.and(Filters.eq(
                                    SearchableField.TITLE, FOLDERNAME),
                            Filters.eq(SearchableField.TRASHED, false)))
                    .build();

            DriveApi.MetadataBufferResult result = Drive.DriveApi.query(mGoogleApiClient, query).await();
            isFound = false;
            if (!result.getStatus().isSuccess()) {
                showMessage("Cannot create folder in the root.");
            } else {
                showMessage("Found metadata:");
                for (Metadata m : result.getMetadataBuffer()) {
                    if (m.getTitle().equals(FOLDERNAME)) {
                        showMessage(m.getTitle());
                        folderId = m.getDriveId().encodeToString();
                        showMessage("Folder exists");
                        isFound = true;
                        Query query2 = new Query.Builder()
                                .addFilter(Filters.eq(SearchableField.TITLE, FILENAME))
                                .build();
                        DriveApi.MetadataBufferResult result1 = Drive.DriveApi.query(mGoogleApiClient, query2).await();
                        if (!result1.getStatus().isSuccess()) {
                            showMessage("Cannot create folder in the root.");
                        } else {
                            showMessage("outside for");
                            for (Metadata m1 : result1.getMetadataBuffer()) {
                                showMessage("inside for: "+m1.getTitle()+","+ FILENAME);
                                if (m1.getTitle().equals(FILENAME)) {
                                    fileId = m1.getDriveId().encodeToString();
                                    showMessage("File exists");
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
                if (!isFound) {
                    showMessage("Folder not found; creating it.");
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(FOLDERNAME)
                            .build();

                    Drive.DriveApi.getRootFolder(mGoogleApiClient)
                            .createFolder(mGoogleApiClient, changeSet).await();

                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the folder");
                    } else {
                        showMessage("Created a folder");
                        DriveApi.MetadataBufferResult result2 = Drive.DriveApi.query(mGoogleApiClient, query).await();

                        if (!result2.getStatus().isSuccess()) {
                            showMessage("Cannot create folder in the root.");
                        } else {
                            for (Metadata m : result2.getMetadataBuffer()) {
                                if (m.getTitle().equals(FOLDERNAME)) {
                                    showMessage("Folder exists");
                                    folderId = m.getDriveId().encodeToString();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return new String();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (isFound) {
                new RetrieveDriveFileContentsAsyncTask(
                        AddTicket.this).execute(DriveId.decodeFromString(fileId));
            } else {
                Drive.DriveApi.newDriveContents(mGoogleApiClient)
                        .setResultCallback(driveContentsCallback);
            }
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    DriveFolder folder = DriveId.decodeFromString(folderId).asDriveFolder();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(FILENAME)
                            .setMimeType("text/plain")
                            .setStarred(true).build();
                    folder.createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                            .setResultCallback(fileCallback);
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback =
            new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file: " + result.getDriveFile().getDriveId());
                    fileId = result.getDriveFile().getDriveId().encodeToString();
                    new RetrieveDriveFileContentsAsyncTask(
                            AddTicket.this).execute(DriveId.decodeFromString(fileId));
                }
            };

    final private class RetrieveDriveFileContentsAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, String> {

        public RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackgroundConnected(DriveId... params) {
            String contents = null;
            DriveFile file = params[0].asDriveFile();
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(driveContents.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append(System.getProperty("line.separator"));
                }
                contents = builder.toString();
            } catch (IOException e) {
                Log.e(TAG, "IOException while reading from the stream", e);
            }

            driveContents.discard(getGoogleApiClient());
            return contents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                showMessage("Error while reading from the file");
                return;
            }
            MyApplication app = (MyApplication)getApplication();
            if (app.getInformation().currentTicket.valid)
                driveData = result + Storager.TicketToText(app.getInformation().currentTicket);
            new EditContentsAsyncTask(AddTicket.this).execute(DriveId.decodeFromString(fileId).asDriveFile());
        }
    }

    public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            try {
                DriveApi.DriveContentsResult driveContentsResult = file.open(
                        getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
                if (!driveContentsResult.getStatus().isSuccess()) {
                    return false;
                }
                DriveContents driveContents = driveContentsResult.getDriveContents();
                OutputStream outputStream = driveContents.getOutputStream();
                outputStream.write(driveData.getBytes());
                com.google.android.gms.common.api.Status status =
                        driveContents.commit(getGoogleApiClient(), null).await();
                return status.getStatus().isSuccess();
            } catch (IOException e) {
                Log.e(TAG, "IOException while appending to the output stream", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                showMessage("Error while editing contents");
                return;
            }
            showMessage("Successfully edited contents");
            //back to home screen
            downloadResults.setVisibility(View.INVISIBLE);
            MyApplication app = (MyApplication)getApplication();
            app.getInformation().updated = false;
            Intent intent = new Intent(AddTicket.this, MeLotto.class);
            startActivity(intent);
        }
    }
}

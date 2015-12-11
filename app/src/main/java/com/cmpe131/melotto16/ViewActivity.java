package com.cmpe131.melotto16;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Scanner;

import control.MyApplication;
import control.Storager;
import control.Ticket;

public class ViewActivity extends MyActivity  {

    private static final String TAG = "ViewActivity";
    public static final String PHOTOFOLDER = "App5_Photos";
    public static final String FOLDERNAME = "App5";
    public static final String FILENAME = "App5_data.txt";

    private ArrayList<String> results;
    private ArrayAdapter<String> adapter;
    private TextView downloadResults;
    private ArrayList<Ticket> tickets;

    private String folderId;
    private String fileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        downloadResults = (TextView)findViewById(R.id.txt_downloadTickets);
        downloadResults.setVisibility(View.INVISIBLE);

        ListView listView = (ListView) findViewById(R.id.listTickets);
        results = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        listView.setAdapter(adapter);
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        MyApplication app = (MyApplication)getApplication();
        if (!app.getInformation().updated) {
            downloadResults.setVisibility(View.VISIBLE);
            new CheckIfDataExistsAsyncTask(ViewActivity.this).execute("");
        }
        else
        {
            generateTicketList(app.getInformation().tickets);
        }
    }

    public void showMessage(String message) {
        Log.i(TAG, message);
        //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
                            for (Metadata m1 : result1.getMetadataBuffer()) {
                                showMessage("inside for: "+m1.getTitle()+","+ FILENAME);
                                if (m1.getTitle().equals(FILENAME)) {
                                    fileId = m1.getDriveId().encodeToString();
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
                        ViewActivity.this).execute(DriveId.decodeFromString(fileId));
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
                            ViewActivity.this).execute(DriveId.decodeFromString(fileId));
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
            tickets = new ArrayList<>();
            Scanner in = new Scanner(result);
            while (in.hasNextLine()) {
                String temp = in.nextLine();
                showMessage(temp);
                tickets.add(0, Storager.TextToTicket(temp));
                showMessage("Yo");
            }
            app.getInformation().tickets = tickets;
            generateTicketList(tickets);
            app.getInformation().updated = true;
            downloadResults.setVisibility(View.INVISIBLE);
        }

    }

    private void generateTicketList(ArrayList<Ticket> tickets)
    {
        for (Ticket t : tickets)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(t.ticketType);
            sb.append("\n");
            sb.append("Draw Date: ");
            sb.append(t.drawMonth);
            sb.append(" ");
            sb.append(t.drawDay);
            sb.append(", ");
            sb.append(t.drawYear);
            sb.append("\nLotto Numbers: ");
            for (Integer i : t.numbers[0]) {
                sb.append(i);
                sb.append(" ");
            }
            sb.append("     ");
            sb.append(t.specialNumber[0]);
            sb.append("\n");
            sb.append("Valid for ");
            sb.append(t.draws);
            sb.append(" draw(s). \n");
            for (String s : t.amountWon)
            {
                if (!s.equals("$0") && !s.equals("N/A") && s != null) {
                    sb.append("Won. ");
                    sb.append(s);
                    sb.append(", ");
                }
            }
            for (boolean b : t.isDrawn)
            {
                if (!b) {
                    sb.append("Results still pending.\n");
                    break;
                }
            }
            sb.append(t.imageLink.size());
            sb.append(" Attached photo(s).");
            results.add(sb.toString());
            adapter.notifyDataSetChanged();
        }
    }
}

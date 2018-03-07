package sandoval.cis2237.com.timekepping_bysalvador;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;
// need to create a project for api 19

public class MainActivity extends AppCompatActivity {
    private ListView listView;  // listview for the main screen
    private ListView lvTotalPerDate;  // listview for the second screen
    private TimeKeepinDbAdapter dbAdapter;
    private TimeKeepinSimpleCursorAdapter timeKeepinSimpleCursorAdapter;
    private SimpleCursorAdapterForTotalPerDate simplecursoradapterfortotalperdate;
    private static int number = 0;
    private TextView tvTotalHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setContentView(R.layout.activity_main);

        tvTotalHours = (TextView) findViewById(R.id.number);


        // check if database is open
        dbAdapter = new TimeKeepinDbAdapter(this);
        dbAdapter.open();
        dbAdapter.deleteAllReminders();
        if(savedInstanceState == null){
            dbAdapter.timeEntry("12/04/2016","11:00","11:00",12,12);
            dbAdapter.timeEntry("12/04/2016","11:00","11:00",12,12);
            dbAdapter.timeEntry("12/04/2016","11:00","11:00",12,12);
            dbAdapter.timeEntry("12/06/2016","11:00","11:00",12,12);
            dbAdapter.timeEntry("12/06/2016","11:00","11:00",12,12);
            dbAdapter.timeEntry("12/04/2016","11:00","11:00",12,12);
        }

        String [] from = new String []{TimeKeepinDbAdapter.COL_DATE, };

        int [] to = new int [] {R.id.text_holder};

        listView = (ListView) findViewById(R.id.listView);

        Cursor cursor = dbAdapter.fetchAllReminders();

        timeKeepinSimpleCursorAdapter = new TimeKeepinSimpleCursorAdapter(MainActivity.this,R.layout.data_holder,cursor,from,to,0);

        int totalHours,totalMinutes= 0;
        // this get the total hours and minutes in the database
        totalHours = dbAdapter.totalHours();
        totalMinutes = dbAdapter.totalMinutes();

        listView.setAdapter(timeKeepinSimpleCursorAdapter);
               // this part does the math to add hours
        tvTotalHours.setText(GetFormatedTime(totalHours,totalMinutes));
             number = 0;
              // listener for listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // main dialog start
                 number = (int)id;
                AlertDialog.Builder mainBuilder = new AlertDialog.Builder(MainActivity.this);
                mainBuilder.setMessage("Editar record por fecha");
                // this button for edit
                mainBuilder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // this will trigger the second screen
                        CustomeDialogShowTiMePerDate(number);

                    }
                });
               // this button for delete
               mainBuilder.setNegativeButton("Borrar" ,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // this is the second
                        final AlertDialog.Builder borrarDialog = new AlertDialog.Builder(MainActivity.this);
                        borrarDialog.setMessage("Esto borrara todo los records bajo esta fecha");
                               // this part is to confirm if user want to delete all the record under the same date
                               // if customer said yes delete all time with the same date
                        borrarDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                  // delete and update cursor
                                 String date = " ";
                                 date = dbAdapter.getDateByid(number);
                                 dbAdapter.deleteTimeByDate(date);
                                 timeKeepinSimpleCursorAdapter.changeCursor(dbAdapter.fetchAllReminders());
                                 listView.setAdapter(timeKeepinSimpleCursorAdapter);
                                 int totalHours = dbAdapter.totalHours();
                                int totalMinutes = dbAdapter.totalMinutes();
                                // this part does the math to add hours
                                tvTotalHours.setText(GetFormatedTime(totalHours,totalMinutes));

                                 dialog.dismiss();
                            }
                        });
                               // if no dialog go away
                        borrarDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss(); // this part dismiss the dialogs
                            }
                        });
                        borrarDialog.show();
                    }
                })  ;

                mainBuilder.show();

            } });

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             TimeKeeping timeKeeping = new TimeKeeping("new Entry","12:00","12:00");
                CustomeDialogEditTime(timeKeeping);
            }
        });
    }
// method return formated time
    public String GetFormatedTime(int hours, int minutes){

        // this part does the math to add hours
        if (minutes >= 60){

            int tempMinutes,tempHours = 0;
            tempHours = (minutes / 60) + hours;
            tempMinutes = hours % 60;
            return " " + tempHours + ":" + tempMinutes;

        }else{

            return " " + hours + ":" + minutes;

        }

    }
     // this dialog show another array adapter with time log by date and add button to add
    // time on the same date
    public void CustomeDialogShowTiMePerDate(int id){

        // gettting the date by id
        final String date = dbAdapter.getDateByid(id);
        // getting the dialog and setting the layout
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.total_per_date);
        // item in the dialog
        TextView tvDate = (TextView) dialog.findViewById(R.id.date_total_per_date);
        final TextView tvTotalHoursPerDate = (TextView) dialog.findViewById(R.id.number_total_per_date);
        FloatingActionButton fabTotalPerDate = (FloatingActionButton) dialog.findViewById(R.id.fab_total_per_date);
         lvTotalPerDate = (ListView) dialog.findViewById(R.id.listView2);
         // this textview is to set the date for the array adapter
        tvDate.setText(date);

        tvTotalHoursPerDate.setText(" Total de horas "+ GetFormatedTime(dbAdapter.totalHoursPerDate(date),dbAdapter.totalMinutesPerDate(date)));

        String [] from =  new String []{TimeKeepinDbAdapter.COL_TIME_IN,TimeKeepinDbAdapter.COL_TIME_OUT};

        int [] to = new int[] {R.id.text_holder_2,R.id.text_holder_3};

        Cursor cursor1 = dbAdapter.fetchAllRemindersByDate(date);

        simplecursoradapterfortotalperdate = new SimpleCursorAdapterForTotalPerDate(MainActivity.this,R.layout.data_holder_total_per_date,cursor1,from,to,0);

        lvTotalPerDate.setAdapter(simplecursoradapterfortotalperdate);
        // resetting the values for the id
              number = 0;
        lvTotalPerDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // main dialog start
                number = (int)id;
                // this dialog ask
                AlertDialog.Builder mainBuilder = new AlertDialog.Builder(MainActivity.this);
                // this button for edith
                mainBuilder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // this will trigger the second screen
                        TimeKeeping timeKeeping = dbAdapter.timeKeepingById(number);
                        CustomeDialogEditTime(timeKeeping);
                         // this part does the math to add hours
                        tvTotalHoursPerDate.setText(" Total de horas "+ GetFormatedTime(dbAdapter.totalHoursPerDate(date),dbAdapter.totalMinutesPerDate(date)));
                        // this part does the math to add hours
                        tvTotalHours.setText(GetFormatedTime(dbAdapter.totalHours(),dbAdapter.totalMinutes()));
                        timeKeepinSimpleCursorAdapter.changeCursor(dbAdapter.fetchAllReminders());
                        listView.setAdapter(timeKeepinSimpleCursorAdapter);
                        simplecursoradapterfortotalperdate.changeCursor(dbAdapter.fetchAllRemindersByDate(timeKeeping.getDate()));
                        lvTotalPerDate.setAdapter(simplecursoradapterfortotalperdate);
                        dialog.dismiss();
                    }
                });
                // this button for delete
                mainBuilder.setNegativeButton("Borrar" ,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // this is the second
                        final AlertDialog.Builder borrarDialog = new AlertDialog.Builder(MainActivity.this);
                        borrarDialog.setMessage("Borrar el record");
                        // this part is to confirm if user want to delete all the record under the same date
                        // if customer said yes delete all time with the same date
                        borrarDialog.setPositiveButton("Si",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete and update cursor
                                final String date = dbAdapter.getDateByid(number);
                                    dbAdapter.deleteTimeKeepingById(number);
                                    timeKeepinSimpleCursorAdapter.changeCursor(dbAdapter.fetchAllReminders());
                                    listView.setAdapter(timeKeepinSimpleCursorAdapter);
                                    int totalHours = dbAdapter.totalHours();
                                    int totalMinutes = dbAdapter.totalMinutes();
                                    // this part does the math to add hours
                                    tvTotalHoursPerDate.setText(" Total de horas "+ GetFormatedTime(dbAdapter.totalHoursPerDate(date),dbAdapter.totalMinutesPerDate(date)));
                                    // this part does the math to add hours
                                    tvTotalHours.setText(GetFormatedTime(totalHours,totalMinutes));

                                    dialog.dismiss();


                                simplecursoradapterfortotalperdate.changeCursor(dbAdapter.fetchAllRemindersByDate(date));
                                ;
                                lvTotalPerDate.setAdapter(simplecursoradapterfortotalperdate);


                                dialog.dismiss();
                            }
                        });
                        // if no dialog go away
                        borrarDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // this part dismiss the dialogs
                            }
                        });
                        borrarDialog.show();
                    }
                })  ;

                mainBuilder.show();
            }
        });

        fabTotalPerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeKeeping timeKeeping = new TimeKeeping(date,"10:00","10:00");
                CustomeDialogEditTime(timeKeeping);
            }
        });
        dialog.show();
// end of public void CustomeDialogShowTiMePerDate(int id){
    }
      // this dialog is where user modify or add time and modify time
    public void CustomeDialogEditTime(final TimeKeeping timeKeeping) {
//if the user want to add another time with new date
        if (timeKeeping.getDate() == "new Entry") {
            // getting the dialog and setting the layout
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.edit_time);

            //items in the dialog
            final CalendarView calendarView = (CalendarView) dialog.findViewById(R.id.calendarView);
            final NumberPicker inHour = (NumberPicker) dialog.findViewById(R.id.inHour);
            final NumberPicker inMinutes = (NumberPicker) dialog.findViewById(R.id.inMinutes);
            final NumberPicker outHour = (NumberPicker) dialog.findViewById(R.id.outHour);
            final NumberPicker outMinutes = (NumberPicker) dialog.findViewById(R.id.outMinutes);
            Button saveButton = (Button) dialog.findViewById(R.id.save);
            Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

            // setting values for the number picker, buttons
            saveButton.setText("Guardar");
            cancelButton.setText("Cancelar");
            inHour.setMaxValue(24);
            inHour.setMinValue(0);
            outHour.setMaxValue(24);
            outHour.setMinValue(0);
            inMinutes.setMaxValue(59);
            inMinutes.setMinValue(00);
            outMinutes.setMaxValue(59);
            outMinutes.setMinValue(00);
            // this part enter the date
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    String date = (month + 1) + "/" + dayOfMonth + "/" + year;
                    timeKeeping.setDate(date);
                }
            });

            //  save button check if the values are correct
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // gettting the values in to the timekeeping class
                    int numInHours, numInMinutes, numOutHours, numOutMinutes = 0;
                    numInHours = inHour.getValue();
                    numInMinutes = inMinutes.getValue();
                    numOutHours = outHour.getValue();
                    numOutMinutes = outMinutes.getValue();
                    // checking if the numbers are valid
                    if (numInHours > numOutHours) {
                        Toast.makeText(MainActivity.this, "La hora de entrada no puede ser menor que la de salida", Toast.LENGTH_LONG).show();
                    } else if (numInHours == numOutHours && numInMinutes == numOutMinutes) {
                        Toast.makeText(MainActivity.this, "La hora de entrada y de salida no puede ser la misma", Toast.LENGTH_LONG).show();
                    } else if (timeKeeping.getDate() == "new Entry") {
                        Toast.makeText(MainActivity.this, "Falta escoger la fecha", Toast.LENGTH_LONG).show();
                    } else if (numInHours == numOutHours && numInMinutes > numOutHours) {
                        Toast.makeText(MainActivity.this, "Los minutos de entrada no puede ser menor que la de salida cuando tienen la misma hora", Toast.LENGTH_LONG).show();
                    } else {
                        timeKeeping.setTime(numInHours + ":" + numInMinutes, numOutHours + ":" + numOutMinutes);
                        dbAdapter.timeEntry(timeKeeping);
                        dialog.dismiss();
                        timeKeepinSimpleCursorAdapter.changeCursor(dbAdapter.fetchAllReminders());
                        listView.setAdapter(timeKeepinSimpleCursorAdapter);
                        // getting the values from the database
                        int totalHours = dbAdapter.totalHours();
                        int totalMinutes = dbAdapter.totalMinutes();
                        // this part does the math to add hours
                        tvTotalHours.setText(GetFormatedTime(totalHours, totalMinutes));

                    }
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
// if the user want to modify the time and add time under the same date
        }else{
            // getting the dialog and setting the layout
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.edit_time_under_the_same_date);

            //items in the dialog
            final NumberPicker inHour = (NumberPicker) dialog.findViewById(R.id.inHour);
                  TextView title = (TextView) dialog.findViewById(R.id.title);
            final NumberPicker inMinutes = (NumberPicker) dialog.findViewById(R.id.inMinutes);
            final NumberPicker outHour = (NumberPicker) dialog.findViewById(R.id.outHour);
            final NumberPicker outMinutes = (NumberPicker) dialog.findViewById(R.id.outMinutes);
            Button saveButton = (Button) dialog.findViewById(R.id.save);
            Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

            // setting values for the number picker, buttons
            saveButton.setText("Guardar");
            cancelButton.setText("Cancelar");
            inHour.setMaxValue(24);
            inHour.setMinValue(0);
            outHour.setMaxValue(24);
            outHour.setMinValue(0);
            inMinutes.setMaxValue(59);
            inMinutes.setMinValue(00);
            outMinutes.setMaxValue(59);
            outMinutes.setMinValue(00);
            // set titles for the dialog
            title.setText(timeKeeping.getDate());
            String inTime = timeKeeping.getInTime();
            String outTime = timeKeeping.getOutTime();
            StringTokenizer stInTime = new StringTokenizer(inTime,":");
            StringTokenizer stOutTime = new StringTokenizer(outTime,":");

            String numHourIn = stInTime.nextToken();
            String numMinIn = stInTime.nextToken();

            String numHourOut = stOutTime.nextToken();
            String numMinOut = stOutTime.nextToken();

            int numinHours = Integer.parseInt(numHourIn);
            int numinMin = Integer.parseInt(numMinIn);
            int numoutHours = Integer.parseInt(numHourOut);
            int numoutMin = Integer.parseInt(numMinOut);

            inHour.setValue(numinHours);
            inMinutes.setValue(numinMin);
            outHour.setValue(numoutHours);
            outMinutes.setValue(numoutMin);
            //  save button check if the values are correct
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // checking if hours are in range
                    if (inHour.getValue() > outHour.getValue()) {
                        Toast.makeText(MainActivity.this, "La hora de entrada no puede ser mayor que la de salida", Toast.LENGTH_LONG).show();
                    } else if (inHour.getValue() == outHour.getValue() && inMinutes.getValue() == outMinutes.getValue()) {
                        Toast.makeText(MainActivity.this, "La hora de entrada y de salida no puede ser la misma", Toast.LENGTH_LONG).show();
                    }else if (inHour.getValue() == outHour.getValue() && inMinutes.getValue() > outHour.getValue()) {
                        Toast.makeText(MainActivity.this, "Los minutos de entrada no puede ser mayor que la de salida cuando tienen la misma hora", Toast.LENGTH_LONG).show();
                    } else {

                        if(timeKeeping.getOutTime() == "10:00" && timeKeeping.getInTime() == "10:00" ){
                            timeKeeping.setTime(inHour.getValue() + ":" + inMinutes.getValue(), outHour.getValue() + ":" + outMinutes.getValue());
                            dbAdapter.timeEntry(timeKeeping);
                        }else{
                            timeKeeping.setTime(inHour.getValue() + ":" + inMinutes.getValue(), outHour.getValue() + ":" + outMinutes.getValue());
                            dbAdapter.updateTimeKeeping(timeKeeping);
                        }

                        //simplecursoradapterfortotalperdate.changeCursor(dbAdapter.fetchAllRemindersByDate(timeKeeping.getDate()));
                        //lvTotalPerDate.setAdapter(simplecursoradapterfortotalperdate);
                        dialog.dismiss();
                    }
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
// end of public void CustomeDialogEditTime(final TimeKeeping timeKeeping) {
}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbAdapter.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           dbAdapter.deleteAllReminders();
        }

        return true;
    }
}

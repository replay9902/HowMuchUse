package com.example.howmuchuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    // Millisecond 형태의 하루(24 시간)
    private final int ONE_DAY = 24 * 60 * 60 * 1000;
    private final String TAG = "MainActivity";
    // 현재 날짜를 알기 위해 사용
    private Calendar mCalendar;

    // D-day result
    private TextView mTvResult;
    private TextView tvToday;
    private TextView tvDate;
    private EditText budgetET;
    private Button budgetBtn;

    private SharedPreferences sp;
    String budget = null;
    String dday = null;

    // DatePicker 에서 날짜 선택 시 호출
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker a_view, int a_year, int a_monthOfYear, int a_dayOfMonth) {
            // D-day 계산 결과 출력
            mTvResult.setText(getDday(a_year, a_monthOfYear, a_dayOfMonth));
            dday = getSelectDay(a_year, a_monthOfYear, a_dayOfMonth);

            sp = getSharedPreferences("myfile", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("dday", dday);
            editor.commit();

            dday = sp.getString("dday","");
            tvDate.setText("선택된 날짜 : " + dday);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 한국어 설정 (ex: date picker)
        Locale.setDefault(Locale.KOREAN);

        // 현재 날짜를 알기 위해 사용
        mCalendar = new GregorianCalendar();

        // Today 보여주기
        tvToday = findViewById(R.id.tv_today);
        tvDate = findViewById(R.id.tv_date);

        tvToday.setText("오늘 날짜 : " + getToday());

        // D-day 보여주기
        mTvResult = findViewById(R.id.tv_result);

        //예산 등록 버튼
        budgetBtn = findViewById(R.id.budget_btn);
        budgetET = findViewById(R.id.budget_et);


        SharedPreferences sp = getSharedPreferences("myfile", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        budget = sp.getString("budget","");
        budgetET.setText(budget);

        // Input date click 시 date picker 호출
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {
                final int year = mCalendar.get(Calendar.YEAR);
                final int month = mCalendar.get(Calendar.MONTH);
                final int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, mDateSetListener, year, month, day);
                dialog.show();


            }
        };
        findViewById(R.id.btn_input_date).setOnClickListener(clickListener);

        //예산 등록 버튼 눌렀을 때
        budgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = budgetET.getText().toString();

                SharedPreferences sp = getSharedPreferences("myfile", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("budget", value);
                editor.commit();
                // 저장 버튼 누른 후 키보드 안보이게 하기
                InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
                imm.hideSoftInputFromWindow( budgetET.getWindowToken(), 0 );
            }
        });
    }

    /**
     * Today 반환
     */
    private String getToday() {
        // 지정된 format 으로 string 표시
        final String strFormat = getString(R.string.format_today);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat(strFormat);
        return CurDateFormat.format(mCalendar.getTime());
    }

    /**
     * 선택된 날짜 반환
     */
    private String getSelectDay(int a_year, int a_monthOfYear, int a_dayOfMonth){

        final Calendar selectCalendar = Calendar.getInstance();
        selectCalendar.set(a_year, a_monthOfYear, a_dayOfMonth);

        final String strFormat = getString(R.string.format_today);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat(strFormat);

        return CurDateFormat.format(selectCalendar.getTime());
    }

    /**
     * D-day 반환
     */
    private String getDday(int a_year, int a_monthOfYear, int a_dayOfMonth) {
        // D-day 설정
        final Calendar ddayCalendar = Calendar.getInstance();
        ddayCalendar.set(a_year, a_monthOfYear, a_dayOfMonth);

        // D-day 를 구하기 위해 millisecond 으로 환산하여 d-day 에서 today 의 차를 구한다.
        final long dday = ddayCalendar.getTimeInMillis() / ONE_DAY;
        final long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;
        long result = dday - today;

        // 출력 시 d-day 에 맞게 표시
        final String strFormat;
        if (result > 0) {
            strFormat = "D-%d";
        } else if (result == 0) {
            strFormat = "D-Day";
        } else {
            result *= -1;
            strFormat = "D+%d";
        }

        final String strCount = (String.format(strFormat, result));
        return strCount;
    }
}
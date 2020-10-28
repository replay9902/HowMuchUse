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
import android.widget.Toast;

import java.text.ParseException;
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


    //ui-component
    private TextView tvToday;               //오늘 날짜 표시 텍스트 뷰
    private TextView tvDate;                //선택된 날짜 표시 텍스트 뷰
    private TextView tvDday;                //D-day 표시 텍스트 뷰
    private TextView tvMoneyPerDay;         //하루당 금액 표시 텍스트 뷰 
    private TextView tvMoneyForRemain;      //남은 금액 표시 텍스트 뷰

    private EditText etBudget;              //예산 입력 에디트텍스트
    private EditText etBalance;             //잔액 입력 에디트텍스트

    private Button btnStartDate;            //시작 날짜 선택 버튼
    private Button btnEndDate;              //종료 날짜 선택 버튼
    private Button btnBudget;               //예산 적용 버튼
    private Button btnBalance;              //잔액 적용 버튼

    //variables
    private SharedPreferences sp;
    String budget = null;                   //예산 값
    String startDay = null;                 //시작날짜 값
    String endDay = null;                   //종료날짜 값
    String balance = null;                  //잔액 값
    String moneyPerDay = null;              //하루 당 금액 값
    String moneyForRemain = null;           //디데이당 남은 금액 값


    // DatePicker 에서 시작날짜 선택 시 호출
    private DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker a_view, int a_year, int a_monthOfYear, int a_dayOfMonth) {

            final Calendar stCalendar = Calendar.getInstance();
            stCalendar.set(a_year, a_monthOfYear, a_dayOfMonth);

            final String strFormat = getString(R.string.format_date);
            SimpleDateFormat stDateFormat = new SimpleDateFormat(strFormat);

            startDay = stDateFormat.format(stCalendar.getTime());

            //sp에 dday값 저장
            sp = getSharedPreferences("myfile", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("startDay", startDay);
            editor.commit();

            endDay = sp.getString("endDay", "");

            //tvDate에 선택된 날짜 표시
            String text = "시작 날짜 : " + startDay;

            if(endDay != null){
                text += " 종료 날짜 : " + endDay;
            }
            tvDate.setText(text);

            Long term = null;
            //tvDday에 D-day 표시

            try {
                term = getDateCalculation(startDay, endDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }

             tvDday.setText(String.valueOf(term));

            //tvMoneyPerDay 에 하루당 금액 표시
            if(budget != "" && startDay != "" && endDay != "") {
                long lbudget = Long.parseLong(budget);
                double dmoneyPerDay = lbudget / term;
                //Integer imoneyPerDay = Integer.parseInt(String.valueOf(_moneyPerDay));
                moneyPerDay = Double.toString(dmoneyPerDay);
                tvMoneyPerDay.setText(term + " 일간" + budget + "원을 가지고  하루에 " + moneyPerDay + "원씩 쓸수 있어요.");
            }





            //Toast.makeText(MainActivity.this,"예산:" + budget, Toast.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this,"잔액:" + balance, Toast.LENGTH_SHORT).show();
        }
    };

    // DatePicker 에서 종료 날짜 선택 시 호출
    private DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker a_view, int a_year, int a_monthOfYear, int a_dayOfMonth) {


            final Calendar edCalendar = Calendar.getInstance();
            edCalendar.set(a_year, a_monthOfYear, a_dayOfMonth);

            final String strFormat = getString(R.string.format_date);
            SimpleDateFormat stDateFormat = new SimpleDateFormat(strFormat);

            endDay = stDateFormat.format(edCalendar.getTime());


            //sp에 dday값 저장
            sp = getSharedPreferences("myfile", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("endDay", endDay);
            editor.commit();

            startDay = sp.getString("startDay", "");

            //tvDate에 선택된 날짜 표시
            String text = "시작 날짜 : " + startDay;

            if(endDay != null){
                text += " 종료 날짜 : " + endDay;
            }
            tvDate.setText(text);

            Long term = null;
            //tvDday에 D-day 표시

            try {
                term = getDateCalculation(startDay, endDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvDday.setText(String.valueOf(term));

            //tvMoneyPerDay 에 하루당 금액 표시
            if(budget != "" && startDay != "" && endDay != "") {
                long lbudget = Long.parseLong(budget);
                double dmoneyPerDay = lbudget / term;
                //Integer imoneyPerDay = Integer.parseInt(String.valueOf(_moneyPerDay));
                moneyPerDay = Double.toString(dmoneyPerDay);
                tvMoneyPerDay.setText(term + " 일간" + budget + "원을 가지고  하루에 " + moneyPerDay + "원씩 쓸수 있어요.");
            }







            //Toast.makeText(MainActivity.this,"예산:" + budget, Toast.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this,"잔액:" + balance, Toast.LENGTH_SHORT).show();
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

        // 컴포넌트 정의
        tvToday = findViewById(R.id.tv_today);
        tvDate = findViewById(R.id.tv_date);
        tvDday = findViewById(R.id.tv_dday);
        tvMoneyPerDay = findViewById(R.id.tv_money_for_day);
        tvMoneyForRemain = findViewById(R.id.tv_money_for_remain);
        etBudget = findViewById(R.id.et_budget);
        etBalance = findViewById(R.id.et_balance);
        btnStartDate = findViewById(R.id.btn_input_start_date);
        btnEndDate = findViewById(R.id.btn_input_end_date);
        btnBudget = findViewById(R.id.btn_budget);
        btnBalance = findViewById(R.id.btn_balance);

        
        tvToday.setText("오늘 날짜 : " + getToday());

        // D-day 보여주기

    


        SharedPreferences sp = getSharedPreferences("myfile", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        budget = sp.getString("budget","");
        balance = sp.getString("balance", "");
        startDay = sp.getString("startDay", "");
        endDay = sp.getString("endDay", "");
        etBudget.setText(budget);
        etBalance.setText(balance);

        String text = "";
        if(startDay != ""){
            text += "시작 날짜 : " + startDay;
        }
        if(endDay != ""){
            text += "종료 날짜 : "+ endDay;
        }

        tvDate.setText(text);


        // 시작 날짜 선택 click 시 date picker 호출
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {
                final int year = mCalendar.get(Calendar.YEAR);
                final int month = mCalendar.get(Calendar.MONTH);
                final int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, startDateSetListener, year, month, day);
                dialog.show();


            }
        });

        // 종료 날짜 선택 click 시 date picker 호출
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {
                final int year = mCalendar.get(Calendar.YEAR);
                final int month = mCalendar.get(Calendar.MONTH);
                final int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, endDateSetListener, year, month, day);
                dialog.show();


            }
        });

        //예산 등록 버튼 눌렀을 때
        btnBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sp에 budget값 저장
                String value = etBudget.getText().toString();
                budget = value;
                SharedPreferences sp = getSharedPreferences("myfile", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("budget", value);
                editor.commit();

                // 저장 버튼 누른 후 키보드 안보이게 하기
                InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
                imm.hideSoftInputFromWindow( etBudget.getWindowToken(), 0 );
            }
        });

        //잔액 등록버튼 늘렀을 때
        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = etBalance.getText().toString();

                //sp에 balance값 저장
                SharedPreferences sp = getSharedPreferences("myfile", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("balance", value);
                editor.commit();

                // 저장 버튼 누른 후 키보드 안보이게 하기
                InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
                imm.hideSoftInputFromWindow( etBalance.getWindowToken(), 0 );
            }
        });
    }

    /**
     * Today 반환 (yyyy년 MM월 dd일 E요일) 형태
     */
    private String getToday() {
        // 지정된 format 으로 string 표시
        final String strFormat = getString(R.string.format_today);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat(strFormat);
        return CurDateFormat.format(mCalendar.getTime());
    }

    /**
     * 선택된 날짜 반환 (yyyy년 MM월 dd일 E요일) 형태
     */
    private String getSelectDay(int a_year, int a_monthOfYear, int a_dayOfMonth){

        final Calendar selectCalendar = Calendar.getInstance();
        selectCalendar.set(a_year, a_monthOfYear, a_dayOfMonth);

        final String strFormat = getString(R.string.format_today);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat(strFormat);

        return CurDateFormat.format(selectCalendar.getTime());
    }

    /**
     * D-day 를 숫자값 형태로 반환
     */
    private long getDday(int a_year, int a_monthOfYear, int a_dayOfMonth) {
        // D-day 설정
        final Calendar ddayCalendar = Calendar.getInstance();
        ddayCalendar.set(a_year, a_monthOfYear, a_dayOfMonth);

        // D-day 를 구하기 위해 millisecond 으로 환산하여 d-day 에서 today 의 차를 구한다.
        final long dday = ddayCalendar.getTimeInMillis() / ONE_DAY;
        final long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;
        long result = dday - today;

        return result;
    }

    private long getDateCalculation(String startDateString, String endDateString) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Date startDate = format.parse(startDateString);
        Date endDate = format.parse(endDateString);

        long diff = endDate.getTime() - startDate.getTime();
        long diffDays = diff / ( 24 * 60 * 60 * 1000);

        return diffDays;
    }


    /**
     * 숫자값 형태의 dday를 D-1과 같은 문자형태로 리턴
     * @param result
     * @return
     */
    private String getDdayDisplay(long result){

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
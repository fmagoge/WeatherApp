package com.dmatrix.weatherapp.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.dmatrix.weatherapp.R;
import com.dmatrix.weatherapp.utils.Constants;
import com.dmatrix.weatherapp.utils.PrefereneManager;

public class MeasurementsDialogFragment extends AppCompatDialogFragment {

    private RadioGroup radioGroup;
    private RadioButton radioCelsius, radioFahrenheit;
    private PrefereneManager prefereneManager;
    private MeasurementsDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_measurements_dialog, null);

        builder.setView(view)
                .setTitle("Units of Measurement")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO -
                    }
                });

        radioGroup = view.findViewById(R.id.radioGroupTemp);
        radioCelsius = view.findViewById(R.id.radioCelsius);
        radioFahrenheit = view.findViewById(R.id.radioFahrenheit);
        prefereneManager = new PrefereneManager(getActivity());

        if (prefereneManager.getString("unit") != null) {
            if (prefereneManager.getString("unit").equals(Constants.CELSIUS_SYMBOL)) {
                radioCelsius.setChecked(true);
            } else if (prefereneManager.getString("unit").equals(Constants.FAHRENHEIT_SYMBOL)) {
                radioFahrenheit.setChecked(true);
            }
        }

        radioCelsius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefereneManager.clearPreferences();
                prefereneManager.putString("unit", Constants.CELSIUS_SYMBOL);
                listener.applyMeasurements(true);
            }
        });

        radioFahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefereneManager.clearPreferences();
                prefereneManager.putString("unit", Constants.FAHRENHEIT_SYMBOL);
                listener.applyMeasurements(true);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (MeasurementsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement MeasurementsDialogListener");
        }
    }

    public interface MeasurementsDialogListener{
        void applyMeasurements(boolean unitChosen);
    }
}

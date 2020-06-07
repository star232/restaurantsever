package com.pthhack22.myrestaurantserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pthhack22.myrestaurantserver.common.Common;
import com.pthhack22.myrestaurantserver.model.ServerUserModel;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private static int APP_REQUEST_CODE = 7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private DatabaseReference severRef;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener( listener );
    }

    @Override
    protected void onStop() {
        if (listener != null)
            firebaseAuth.removeAuthStateListener( listener );
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        init();
    }

    private void init() {
        providers = Arrays.asList( new AuthUI.IdpConfig.PhoneBuilder().build() );

        severRef = FirebaseDatabase.getInstance().getReference( Common.SERVER_REF );
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder().setContext( this ).setCancelable( false ).build();
        listener = firebaseAuthLocal -> {
            FirebaseUser user = firebaseAuthLocal.getCurrentUser();
            if (user != null) {
                //Check user from firebase
                checkServerUserFromFirebase( user );
            } else {
                phoneLogin();
            }
        };
    }

    private void checkServerUserFromFirebase(FirebaseUser user) {
        dialog.show();
        severRef.child( user.getUid() )
                .addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ServerUserModel userModel = dataSnapshot.getValue( ServerUserModel.class );
                            if (userModel.isActive()) {
                                goToHomeActivity( userModel );

                            } else {
                                dialog.dismiss();
                                Toast.makeText( MainActivity.this, "You must be allowed from Admin to access this app", Toast.LENGTH_SHORT ).show();
                            }

                        } else {
                            // User not exits in database
                            dialog.dismiss();
                            showRegisterDialog( user );
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                        Toast.makeText( MainActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );
    }

    private void showRegisterDialog(FirebaseUser user) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( this );
        builder.setTitle( "Register" );
        builder.setMessage( "Please fill information \n Admin will accept your account late" );

        View itemView = LayoutInflater.from( this ).inflate( R.layout.layout_register, null );
        EditText edt_name = itemView.findViewById( R.id.edt_name );
        EditText edt_phone = itemView.findViewById( R.id.edt_phone );

        //SEt data
        edt_phone.setText( user.getPhoneNumber() );
        builder.setNegativeButton( "CANCEL", (dialogInterface, i) -> dialogInterface.dismiss() );
        builder.setPositiveButton( "REGISTER", (dialogInterface, i) -> {
            if (TextUtils.isEmpty( edt_name.getText().toString() )) {
                Toast.makeText( MainActivity.this, "Please enter your name ", Toast.LENGTH_SHORT ).show();
                return;
            }

            ServerUserModel serverUserModel = new ServerUserModel();
            serverUserModel.setUid( user.getUid() );
            serverUserModel.setName( edt_name.getText().toString() );
            serverUserModel.setPhone( edt_phone.getText().toString() );
            serverUserModel.setActive( false ); // Default failed, we must active user by manual in Firebase

            dialog.show();

            severRef.child( serverUserModel.getUid() )
                    .setValue( serverUserModel )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText( MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    } ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    Toast.makeText( MainActivity.this, "Congratulation ! Register Success ! Admin will check and active you soon", Toast.LENGTH_SHORT ).show();
                    //goToHomeActivity(serverUserModel);

                }
            } );


        } );

        builder.setView( itemView);

        androidx.appcompat.app.AlertDialog registerDialog = builder.create();
        registerDialog.show();
    }

    private void goToHomeActivity(ServerUserModel serverUserModel) {

        dialog.dismiss();
        Common.currentSeverUser = serverUserModel;
        startActivity( new Intent( this, HomeActivity.class ) );
        finish();

    }

    private void phoneLogin() {
        startActivityForResult( AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders( providers )
                .build(), APP_REQUEST_CODE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent( data );
            if (requestCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            } else {
                Toast.makeText( this, "", Toast.LENGTH_SHORT ).show();
            }

        }
    }
}


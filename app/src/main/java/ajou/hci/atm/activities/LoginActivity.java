package ajou.hci.atm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ajou.hci.atm.R;
import ajou.hci.atm.data.USERDBHelper;
import ajou.hci.atm.model.User;

public class LoginActivity extends BaseActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;
    public DatabaseReference Ajou_DB;
    private USERDBHelper userdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        Ajou_DB = FirebaseDatabase.getInstance().getReference();

        //Google Login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        //checkAuth();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                if (task.isSuccessful()) {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        firebaseAuthWithGoogle(account);
                    }
                }else{
                    Log.i("LoginActivity",task.getException().getMessage());
                }
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void writeNewUser(final FirebaseUser user) {
        try {

            Ajou_DB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //없는 회원일 경우
                    //페이지 이동해서 값 받아오기
                    if(!dataSnapshot.child("User").child(user.getUid()).exists()){
                        User user_1 = new User(user.getDisplayName(),user.getEmail());
                        Ajou_DB.child("User").child(user.getUid()).setValue(user_1);
                        userdbHelper = new USERDBHelper(getApplicationContext(), "USER.db", null, 1);
                        userdbHelper.insert(user.getUid(), user_1);
                        hideProgressDialog();
                        startActivity(new Intent(LoginActivity.this, TimeTableActivity.class));

                    }else{
                        User user_1 = new User(user.getDisplayName(),user.getEmail());

                        Ajou_DB.child("User").child(user.getUid()).setValue(user_1);

                        userdbHelper = new USERDBHelper(getApplicationContext(), "USER.db", null, 1);
                        userdbHelper.insert(user.getUid(), user_1);
                        hideProgressDialog();
                        startActivity(new Intent(LoginActivity.this, TimeTableActivity.class));


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ERROR" + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onResume() {
        //Log.i("LoginActivity", "onResume");
        super.onResume();
        checkAuth();
    }

    private void checkAuth() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userdbHelper = new USERDBHelper(getApplicationContext(), "USER.db", null, 1);
        if (currentUser != null && userdbHelper.getUser(currentUser.getUid()).getEmail() != null) {
            startActivity(new Intent(LoginActivity.this, TimeTableActivity.class));
        } else {
            //Toast.makeText(getApplicationContext(), "로그인이 필요합니다." + userdbHelper.getResult(), Toast.LENGTH_LONG).show();
        }
    }
}

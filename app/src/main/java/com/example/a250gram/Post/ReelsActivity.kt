package com.example.a250gram.Post

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.a250gram.HomeActivity
import com.example.a250gram.Models.Reel
import com.example.a250gram.Models.User
import com.example.a250gram.databinding.ActivityReelsBinding
import com.example.a250gram.utils.REEL
import com.example.a250gram.utils.REEL_FOLDER
import com.example.a250gram.utils.USER_NODE
import com.example.a250gram.utils.uploadVideo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ReelsActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityReelsBinding.inflate(layoutInflater)
    }

    private lateinit var videoUrl: String
    lateinit var progressDialog: ProgressDialog
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadVideo(uri, REEL_FOLDER, progressDialog) { url ->
                if (url != null) {

                    videoUrl = url

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)

        binding.selectReel.setOnClickListener {
            launcher.launch("video/*")
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
            finish()
        }
        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    var user: User = it.toObject<User>()!!

                    val reel: Reel = Reel(videoUrl!!, binding.caption.editText?.text.toString(),user.image!!)
                    Firebase.firestore.collection(REEL).document().set(reel).addOnSuccessListener {
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + REEL)
                            .document()
                            .set(reel).addOnSuccessListener {
                                startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
                                finish()
                            }
                    }
                }

        }
    }
}
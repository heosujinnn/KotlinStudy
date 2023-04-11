package com.soojin.jinstargram.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.soojin.jinstargram.R
import com.soojin.jinstargram.databinding.ActivityAddPhotoBinding
import com.soojin.jinstargram.databinding.ActivityLoginBinding
import com.soojin.jinstargram.navigation.model.contentDTO
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPhotoBinding
    private lateinit var photolaunch: ActivityResultLauncher<Intent>

    private var photoUri: Uri? = null

    var auth:FirebaseAuth?=null
   // var firestore: FirebaseFirestore?=null
    var storage:FirebaseStorage?=null
    var realtimeDatabase: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //스토리지 초기화
        storage = FirebaseStorage.getInstance()
        auth=FirebaseAuth.getInstance()
     //   firestore=FirebaseFirestore.getInstance()
        realtimeDatabase = FirebaseDatabase.getInstance().reference

        photolaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) { //사진 클릭했을 때 (이미지 경로 이쪽으로 넘어옴
                    val intent = result.data
                    photoUri = intent?.data
                    binding.addphotoImage.setImageURI(photoUri)

                } else {
                    //취소버튼 누를때 작동하는 부분
                    finish()
                }
            }

        //오픈 엘범
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photolaunch.launch(photoPickerIntent)


        binding.addphotoBtnUpload.setOnClickListener {
            contentUpload()
        }
    }

    fun contentUpload(){
        var timestamp=SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName="IMAGE_"+timestamp+"_.pnf"
        var storageRef=storage?.reference?.child("images")?.child(imageFileName)

        //1.promise 방식 구글 권장임
        storageRef?.putFile(photoUri!!)?.continueWithTask{ task : Task<UploadTask.TaskSnapshot>->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO=contentDTO()
            contentDTO.imageUrl=uri.toString()
            contentDTO.uid=auth?.currentUser?.uid
            contentDTO.userId=auth?.currentUser?.email

            contentDTO.explain=addphoto_edit_explain.text.toString() //사용자가 입력한 텍스트가 들어가게 됨.
            contentDTO.timestamp=System.currentTimeMillis()


            realtimeDatabase?.child("images")?.push()?.setValue(contentDTO)
            setResult(Activity.RESULT_OK)
            finish()
            //  Toast.makeText(this,getString(R.string.upload_success),Toast.LENGTH_LONG).show()
        }


        //파일업로드 2. Callback 방식 //둘중에 하나 골라서 쓰세요~
//        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {uri ->
//            var contentDTO=contentDTO()
//            contentDTO.imageUrl=uri.toString()
//            contentDTO.uid=auth?.currentUser?.uid
//            contentDTO.userId=auth?.currentUser?.email
//
//            contentDTO.explain=addphoto_edit_explain.text.toString() //사용자가 입력한 텍스트가 들어가게 됨.
//            contentDTO.timestamp=System.currentTimeMillis()
//
//            realtimeDatabase?.child("images")?.push()?.setValue(contentDTO)
//           // firestore?.collection("images")?.document()?.set(contentDTO)
//            setResult(Activity.RESULT_OK)
//            finish()
//            Toast.makeText(this,"hello",Toast.LENGTH_LONG).show()
        }
    }


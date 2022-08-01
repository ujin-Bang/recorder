package com.restart.recorder

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }
    //권한요청시 인자로 들어갈 권한 지정.
    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private var state = State.BEFORE_RECORDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission() //앱 시작하자마자 권한 요청.
        initView()
    }
    private fun requestAudioPermission(){
        //권한 요청시 인자로 요청할권한, 권한코드를 넣어줘야함.권한요청 yes or no 팝업창이 자동으로 뜬다.
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    //권한 요청후 결과값 받아온 함수 오버라이딩 처리해야함.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //리퀘스트코드가 같고, 그랜트리졸트가 같으면 권한이 부여됨.
        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        //아니면 종료.
        if (!audioRecordPermissionGranted){
            finish()
        }

    }

    private fun initView(){
        recordButton.updateIconWithState(state)
    }

    companion object {
       private const val REQUEST_RECORD_AUDIO_PERMISSION = 201 //권한요청시 인자로 들어갈 리퀘스트 코드
    }
}
package com.restart.recorder

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }

    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }

    //권한요청시 인자로 들어갈 권한 지정.
    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private var state = State.BEFORE_RECORDING //Enum class State에서 녹음전 상태를 처음 상태로 설정.
        set(value) { //상황에 따라 달라진 state값은 value에 들어가고 그 값으로 녹음 버튼아이콘 이미지 다시 세팅
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING) //녹음이 종료되거나, 녹음한 내용이 재생중일 때 리셋버튼 활성화
            recordButton.updateIconWithState(value)
        }

    private var recorder: MediaRecorder? =
        null //미디어 레코드 변수선언, 사용하지 않을 때 널로 두는 편이 메모리 관리에 효율적. 오디오,비디오 파일은 용량이 크기 때문.

    private var player: MediaPlayer? = null //재생하기 변수선언

    //녹음하는 오디오파일울 저장할 수 있는 경로 설정하기.
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission() //앱 시작하자마자 권한 요청.
        initView()
        bindViews()
        initVariables()
    }

    private fun requestAudioPermission() {
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
        if (!audioRecordPermissionGranted) {
            finish()
        }

    }

    //앱이 만들어질 때 버튼이미지를 스테이트 값에 맞게 나타내기.
    private fun initView() {
        recordButton.updateIconWithState(state)

    }

    //
    private fun bindViews(){

        //리셋버튼 클릭시
        resetButton.setOnClickListener {
            stopPlaying() //재생을 멈추고
            state = State.BEFORE_RECORDING // 녹음전 상태로 되돌리기

        }

        recordButton.setOnClickListener{
            when(state){
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING ->{
                    startPlaying()
                }
                else -> {
                    stopPlaying()
                }

            }
        }
    }

    private fun initVariables(){
        state = State.BEFORE_RECORDING
    }



    //미디어 레코더 녹음할 수 있는 상태로 초기화, 안드로이드 개발자 문서Recording 따라하기.
    private fun startRecording() {
        recorder = MediaRecorder().apply {

            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)//전역변수로 세팅한 경로
            prepare() //준비
        }
        recorder?.start() //녹음시작
        state = State.ON_RECORDING
    }

    //녹음종료
    private fun stopRecording() {
        recorder?.run {
            stop() //정지
            release() //메모리 해제
        }
        recorder = null //레코더 널처리
        state = State.AFTER_RECORDING
    }


    //재생 시작
    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }
        player?.start()
        state = State.ON_PLAYING
    }

    //재생 중지 : 레코드와 다르게 굳시 stop을 하고 갈 필요가 없음(release사용 =>end로간다)
    private fun stopPlaying() {
        player?.release()
        player = null
        state = State.AFTER_RECORDING
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201 //권한요청시 인자로 들어갈 리퀘스트 코드
    }
}
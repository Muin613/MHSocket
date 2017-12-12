package com.munin.mhsocket

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.munin.mhsocket.socket.SocketManager
import com.munin.mhsocket.socket.interfaces.ISocketListener

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),ISocketListener {
    override fun onSocketState(socketState: Int, description: String?) {
        Log.e("socket", "state$socketState $description")
    }

    override fun sendData(data: ByteArray?) {
        Log.e("socket", "state$data")

    }

    override fun receiveData(data: ByteArray?) {
        Log.e("socket", "receive$data")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        SocketManager.newInstance()
                .setHostPort("192.168.1.1",8888)
                .build()
                .setListener(this)
                .startSocket()
                .setHeart(byteArrayOf(1,2,3,4,5,6,7))
                .startCheckConnect()
                .startHeart()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            SocketManager.newInstance().build().sendByteMsg(byteArrayOf(1,2,3))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        SocketManager.newInstance().build().stopSocket()
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

package com.orhanavan.nfc

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcAdapter = (getSystemService(Context.NFC_SERVICE) as NfcManager).defaultAdapter
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC özelliği desteklenmiyor.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "Lütfen NFC'yi etkinleştirin.", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()
        enableNfc()
    }

    override fun onPause() {
        super.onPause()
        disableNfc()
    }

    private fun disableNfc() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun enableNfc() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        nfcAdapter = nfcManager.defaultAdapter
        nfcAdapter?.let {
            if (!it.isEnabled) {
                Toast.makeText(this, "Lütfen NFC'yi etkinleştirin.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, javaClass).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                val pendingIntent = PendingIntent.getActivity(
                    this, 0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
                val techLists = arrayOf(arrayOf(Ndef::class.java.name))

                nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, techLists)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val action = intent.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_TAG_DISCOVERED == action) {
            setColorGreen()
            setColorRedDelayed()
        }
    }

    private fun setColorGreen() {
        val bg = findViewById<ConstraintLayout>(R.id.background)
        bg.setBackgroundColor(resources.getColor(R.color.green, null))
    }

    private fun setColorRed() {
        val bg = findViewById<ConstraintLayout>(R.id.background)
        bg.setBackgroundColor(resources.getColor(R.color.red, null))
    }

    private fun setColorRedDelayed() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            setColorRed()
        }
    }

}
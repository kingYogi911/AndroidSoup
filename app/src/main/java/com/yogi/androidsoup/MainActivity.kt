package com.yogi.androidsoup

import android.R
import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.yogi.androidsoup.databinding.ActivityMainBinding
import com.yogi.androidsoup.jsoupExtensions.getSpanned
import org.jsoup.Jsoup


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var text = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        binding.tv2.movementMethod = LinkMovementMethod.getInstance()
        Jsoup.parse(html).body().let { body->
            binding.tv2.setText(body.getSpanned().also {
                text = "$it"
                binding.tv1.text = "$it"
            })
        }
        binding.tv2.addTextChangedListener {
            if (text != it.toString()) {
                text = it.toString()
                binding.tv1.text = text
            }
        }
        binding.tv2.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

            override fun onDestroyActionMode(mode: ActionMode?) {}

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                // TODO Auto-generated method stub
                when (item.itemId) {
                    R.id.copy -> {
                        return false
                    }
                    R.id.cut -> {
                        return true
                    }
                    R.id.paste -> {
                        binding.tv2.setText(Jsoup.parse(binding.tv2.text.toString()).body().getSpanned())
                        return false
                    }
                    else -> {}
                }
                return false
            }
        }
        staticContext = requireNotNull(this)
    }

    companion object {
        lateinit var staticContext: Context
        val html = """
            <body contenteditable="true" class="cke_editable cke_editable_themed cke_contents_ltr" spellcheck="false"><p>Check all type of formatting staring with</p><p><span style="font-weight:bold;"><img alt="\sum \prod" src="https://latex.codecogs.com/gif.latex?%5Csum%20%5Cprod">Bold</span></p><p><em>Italic</em>&nbsp;</p><p><span style="text-decoration:underline #2cb115;" class="Underline answer">Underline</span>&nbsp;</p><p><s>StrikeThrough</s></p><p>Subscript X<sub>2</sub></p><p>SuperScript X<sup>2</sup></p><ol><li>Numbered List</li></ol><ul><li>Dot List</li></ul>
            
            <table border="1" cellspacing="1" cellpadding="1" style="width: 500px;">
            
            <tbody>
                  <tr><td>
                        <span style="font-weight:bold;"><em>table (1,1)</em></span>
                      </td>
                      <td>
                        <span style="font-weight:bold;">table (1,2)</span>
                      </td>
                  </tr>
                  
                  <tr><td>
                         <span style="font-weight:bold">table (2,1)</span>
                         <br>
                      </td>
                      <td>
                         <span style="font-weight:bold">table (2,2)</span>
                         <br>
                      </td>
                  </tr>
                  <tr><td>
                          <span style="font-weight:bold">table (3,1)</span>
                          <br> 
                       </td>
                       <td>
                           <span style="font-weight:bold">table (3,2)</span>
                           <br>
                       </td>
                  </tr>
            </tbody>
            </table>
           <p>
           <ul>
             <li>Coffee</li>
             <li>Tea</li>
             <li>Milk</li>
           </ul>
           </p>
           <p>
           <ol>
             <li>Coffee</li>
             <li>Tea</li>
             <li>Milk</li>
             <li>Coffee</li>
             <li>Tea</li>
             <li>Milk</li>
             <li>Coffee</li>
             <li>Tea</li>
             <li>Milk</li>
             <li>Coffee</li>
             <li>Tea</li>
             <li>Milk</li>
           </ol>
           </p>
                      
            </body>
            """.trimIndent()


    }

}
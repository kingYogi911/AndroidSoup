package com.yogi.androidsoup

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.yogi.androidsoup.databinding.ActivityMainBinding
import com.yogi.androidsoup.jsoupExtensions.getSpanned
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        binding.tv2.movementMethod = LinkMovementMethod.getInstance()
        Jsoup.parse(html).body().let {
            binding.tv1.text = it.toString()
            binding.tv2.text = it.getSpanned()
        }
        staticContext= requireNotNull(this)
    }

    companion object {
        lateinit var staticContext:Context
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
           
            <p>SuperScript X<sup>4</sup></p>           
            </body>
            """.trimIndent()


    }

}
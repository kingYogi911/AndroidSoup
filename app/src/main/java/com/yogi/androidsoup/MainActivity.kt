package com.yogi.androidsoup

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.yogi.androidsoup.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var text = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        binding.tv2.movementMethod = LinkMovementMethod.getInstance()
        AndroidSoup.parse(html).let {
            binding.tv2.setText(it)
            text = "$it"
            binding.tv1.text = "$it"
        }
    }

    companion object {
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
                      </td>
                      <td>
                         <span style="font-weight:bold">table (2,2)</span>
                      </td>
                  </tr>
                  <tr><td>
                          <span style="font-weight:bold">table (3,1)</span> 
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
             <li>Coffee lets check if for a whole line or may be for two why the hell i am not able to get it tp two lines</li>
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
           <a href="https://www.w3schools.com">Visit W3Schools.com!</a>
              
              <table>
                  <caption>Alien football stars</caption>
                  <tr>
                      <th scope="col">Player</th>
                      <th scope="col">Gloobles</th>
                      <th scope="col">Za'taak</th>
                  </tr>
                  <tr>
                      <th scope="row">TR-7</th>
                      <td>7</td>
                      <td>4,569</td>
                  </tr>
                  <tr>
                      <th scope="row">Khiresh Odo Yogesh Sahani</th>
                      <td>7</td>
                      <td>7,223</td>
                  </tr>
                  <tr>
                      <th scope="row">Mia Oolong</th>
                      <td>9</td>
                      <td>6,219</td>
                  </tr>
                  <tr>
                      <th scope="row">Mia Oolong</th>
                      <td>9</td>
                      <td>6,219</td>
                  </tr>
              </table>

            </body>
            """.trimIndent()
    }
}
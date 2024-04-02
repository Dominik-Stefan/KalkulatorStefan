package hr.tvz.android.kalkulatorstefan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import hr.tvz.android.kalkulatorstefan.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() , View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    /*
    private lateinit var description : TextView
    private lateinit var output : TextView
    private lateinit var outputResult : TextView
    private lateinit var input : TextView
    private lateinit var inputText : MultiAutoCompleteTextView
    private lateinit var switchMode : ImageButton
    private lateinit var settings : ImageButton
    private lateinit var convert : Button
    private lateinit var copy : ImageButton
    private lateinit var clear : ImageButton
    private lateinit var font : TextView
    private lateinit var fontBar : SeekBar
    private lateinit var themeSwitch : Switch
     */

    private var morse : Boolean = true

    private lateinit var sharedPref: SharedPreferences

    private val morseCodeMap = hashMapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".", 'F' to "..-.", 'G' to "--.",
        'H' to "....", 'I' to "..", 'J' to ".---", 'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.",
        'O' to "---", 'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-", 'U' to "..-",
        'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--", 'Z' to "--..", '0' to "-----",
        '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-", '5' to ".....", '6' to "-....",
        '7' to "--...", '8' to "---..", '9' to "----.", '.' to ".-.-.-", ',' to "--..--", '?' to "..--..",
        '\'' to ".----.", '/' to "-..-.", '(' to "-.--.", ')' to "-.--.-", ':' to "---...", '=' to "-...-",
        '+' to ".-.-.", '-' to "-....-", '"' to ".-..-.", '@' to ".--.-.", 'Ć' to "-.-..", 'Đ' to "..-..",
        'Š' to "----", ' ' to "/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.main

        setContentView(view)

        /*
        description = findViewById(R.id.description)
        output = findViewById(R.id.output)
        outputResult = findViewById(R.id.output_result)
        input = findViewById(R.id.input)
        inputText = findViewById(R.id.input_text)
        switchMode = findViewById(R.id.switch_btn)
        settings = findViewById(R.id.settings_btn)
        convert = findViewById(R.id.convert_btn)
        copy = findViewById(R.id.copy_btn)
        clear = findViewById(R.id.clear_btn)
        font = findViewById(R.id.font_size)
        fontBar = findViewById(R.id.font_size_bar)
        themeSwitch = findViewById(R.id.theme_switch)
         */

        sharedPref = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)

        if(getThemePreferance()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.themeSwitch.isChecked = true
        }

        binding.settingsBtn.setOnClickListener(this)
        binding.convertBtn.setOnClickListener(this)
        binding.copyBtn.setOnClickListener(this)
        binding.clearBtn.setOnClickListener(this)
        binding.switchBtn.setOnClickListener(this)

        binding.fontSize.visibility = View.GONE
        binding.fontSizeBar.visibility = View.GONE
        binding.themeSwitch.visibility = View.GONE

        binding.fontSizeBar.min = 20
        binding.fontSizeBar.max = 50

        binding.fontSizeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress.toFloat())
                    binding.output.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress.toFloat())
                    binding.outputResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress.toFloat())
                    binding.input.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress.toFloat())
                    binding.inputText.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress.toFloat())
                    binding.convertBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress.toFloat())
                    binding.fontSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress.toFloat())
                    binding.themeSwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.themeSwitch.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference(false)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(true)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            binding.switchBtn.id -> {
                binding.outputResult.text = ""
                binding.inputText.setText("")

                val tmp : String = binding.outputResult.hint.toString()
                binding.outputResult.setHint(binding.inputText.hint.toString())
                binding.inputText.setHint(tmp)

                morse = !morse
            }
            binding.settingsBtn.id -> {
                if (binding.fontSize.visibility == View.GONE) {
                    binding.fontSize.visibility = View.VISIBLE
                    binding.fontSizeBar.visibility = View.VISIBLE
                    binding.themeSwitch.visibility = View.VISIBLE
                } else {
                    binding.fontSize.visibility = View.GONE
                    binding.fontSizeBar.visibility = View.GONE
                    binding.themeSwitch.visibility = View.GONE
                }
            }
            binding.convertBtn.id -> {
                if(morse) {
                    binding.outputResult.text = encodeText(binding.inputText.text.toString())
                } else {
                    binding.outputResult.text = decodeMorseCode(binding.inputText.text.toString())
                }
            }
            binding.copyBtn.id -> {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("output", binding.outputResult.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            binding.clearBtn.id -> {
                binding.outputResult.text = ""
                binding.inputText.setText("")
            }
        }
    }

    private fun getThemePreferance(): Boolean {
        return sharedPref.getBoolean("theme_pref", true)
    }

    private fun saveThemePreference(isDarkTheme: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean("theme_pref", isDarkTheme)
        editor.apply()
    }

    private fun encodeText(text: String): String {
        val result = StringBuilder()
        text.uppercase().forEach { char ->
            val code = morseCodeMap[char]
            if (code != null) {
                result.append(code)
                result.append(" ")
            }
        }
        return result.toString().trim()
    }

    private fun decodeMorseCode(morseCode: String): String {
        val words = morseCode.split("/") // word separator
        val result = StringBuilder()
        words.forEach { word ->
            val characters = word.split(" ") // character separator
            characters.forEach { code ->
                val letter = morseCodeMap.filter { it.value == code }.keys.firstOrNull()
                if (letter != null) {
                    result.append(letter)
                }
            }
            result.append(" ")
        }
        return result.toString().trim()
    }

}
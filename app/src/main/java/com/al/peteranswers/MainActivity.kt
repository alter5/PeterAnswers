package com.al.peteranswers

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG_DEBUG_HANDLE_PETITION = "handlePetition"

    private lateinit var editTextPetition: EditText
    private lateinit var editTextQuestion: EditText
    private lateinit var sendButton: Button
    private lateinit var helpButton: Button

    private lateinit var petition: String // "Peter, please answer the following"
    private lateinit var petition_input: String // "Peter, ple
    private var secretMode: Boolean = false // User may press "." on their keyboard to enter secret mode
    private lateinit var secretAnswer: String // Secretly entered answer to be displayed
    private var inputLengthReq = 1 // Prevents editTextPetition.doAfterTextChanged method from entering infinite loop

    private var debugHandlePetitionEnter = 0 // TODO: Delete
    private var debugHandlePetitionExit = 0 // TODO: Delete
    private var debugHandlePetitionEnterSecret = 0 // TODO: Delete
    private var debugHandlePetitionExitSecret = 0 // TODO: Delete

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPetition = edit_text_petition as (EditText)
        editTextQuestion = edit_text_question as (EditText)
        sendButton = button_send as (Button)
        helpButton = button_help as (Button)

        editTextPetition.doAfterTextChanged { petitionText ->
            handlePetition(petitionText)
        }

        sendButton.setOnClickListener(){
            showAnswer()
        }

        helpButton.setOnClickListener(){
            openHelpDialog()
        }

        petition = getString(R.string.petition_string)
        secretAnswer = ""
        answer_text_view.text = " "

        // Disable keyboard suggestions
        editTextPetition.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        editTextQuestion.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }


    private fun handlePetition(text: Editable?) {
        debugHandlePetitionEnter++ // TODO: Delete
        Log.d(TAG_DEBUG_HANDLE_PETITION, "Entered handlePetition: $debugHandlePetitionEnter") // TODO: Delete

        // Extract text from editText
        var text: String = text.toString()
        var text2: String

        // editTextQuestion should remain disabled until a petition has been entered
        editTextQuestion.isEnabled = text.isNotEmpty()

        // To enter secret mode, enter "." as your first character
        if (text == "."){
            secretMode = true

            // "P" from "Peter, ..."
            editTextPetition.setText(petition[0].toString())

            // Moves cursor to end
            editTextPetition.setSelection(text!!.length)

            // Prevents textChanged() method from entering infinite loop
            inputLengthReq++
        }

        if (secretMode && (text.length == inputLengthReq) && text.length != 1){
            debugHandlePetitionEnterSecret++ // TODO: Delete
            Log.d(TAG_DEBUG_HANDLE_PETITION, "Entered handlePetition, secret: $debugHandlePetitionEnterSecret") // TODO: Delete

            inputLengthReq++

            // Remove last char from input
            text2 = text.substring(0, text.length - 1)

            // Append entered char to secretAnswer
            secretAnswer += text.get(text.length - 1)

            // Append petition char to input
            text2 += petition[text.length - 1]
            editTextPetition.setText(text2)

            // Move cursor to end of petition
            editTextPetition.setSelection(text2!!.length)

            debugHandlePetitionEnter++ // TODO: Delete
            Log.d(TAG_DEBUG_HANDLE_PETITION, "Exited handlePetition, secret: " +
                    "$debugHandlePetitionExitSecret, Secret Answer: $secretAnswer, Petition Text: $text2") // TODO: Delete
        } else {
            debugHandlePetitionExit++ // TODO: Delete
            Log.d(TAG_DEBUG_HANDLE_PETITION, "Exited handlePetition normally: $debugHandlePetitionExit") // TODO: Delete
        }


    }

    private fun showAnswer() {
        // Shows answer, and set answerToggle to false
        var answer: String
        if (secretMode){
            answer = secretAnswer
        } else {
            answer = getString(R.string.answer_1)
        }

        // Delete edit texts
        editTextPetition.setText("")
        editTextQuestion.setText("")

        // Present answer
        answer_text_view.resetLoader() // Adds shimmer to text before loading
        answer_text_view.text = answer

        // Reset secret mode attributes
        secretMode = false
        secretAnswer = ""
        inputLengthReq = 1
    }

    private fun openHelpDialog() {
        var dBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        dBuilder.setTitle(R.string.help_dialog_title)
        dBuilder.setMessage(R.string.help_dialog_text)
        dBuilder.setPositiveButton("OK"
        ) { dialog, _ -> dialog.dismiss() }
        dBuilder.show()
    }
}

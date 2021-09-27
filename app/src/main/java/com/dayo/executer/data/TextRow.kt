import android.content.Context
import android.graphics.Color
import android.widget.TableRow
import android.widget.TextView

@Deprecated("This code is deprecated :D")
class TextRow(context: Context, text: String): TableRow(context) {
    val textView = TextView(context)
    private fun addView(){
        super.removeAllViews()
        super.addView(textView)
    }
    init{
        textView.text = text
        textView.setTextColor(Color.BLACK)
        addView()
    }
    companion object {
        fun BlankTableRow(context: Context): TableRow {
            return TextRow(context, " ")
        }
    }
}
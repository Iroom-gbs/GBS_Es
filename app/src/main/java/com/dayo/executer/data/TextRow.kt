import android.content.Context
import android.widget.TableRow
import android.widget.TextView

class TextRow(context: Context, text: String): TableRow(context) {
    val space = TextView(context)
    private fun addView(){
        super.removeAllViews()
        super.addView(space)
    }
    init{
        space.text = text
        addView()
    }
    companion object {
        fun BlankTableRow(context: Context): TableRow {
            return TextRow(context, " ")
        }
    }
}
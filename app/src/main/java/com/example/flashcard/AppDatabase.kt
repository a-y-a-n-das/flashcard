import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.flashcard.CategoryDao
//import com.example.flashcard.Category
import androidx.room.Entity
import androidx.room.PrimaryKey





@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Database(entities = [Category::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}




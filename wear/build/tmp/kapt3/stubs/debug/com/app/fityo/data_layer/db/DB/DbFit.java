package com.app.fityo.data_layer.db.DB;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\u000b"}, d2 = {"Lcom/app/fityo/data_layer/db/DB/DbFit;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "essercissiDao", "Lcom/app/fityo/data_layer/db/dao/DaoEssercissi;", "schedeDao", "Lcom/app/fityo/data_layer/db/dao/DaoSchede;", "notificationsDao", "Lcom/app/fityo/data_layer/db/dao/DaoNotifications;", "Companion", "wear_debug"})
@androidx.room.Database(entities = {com.app.fityo.data_layer.db.EsserciziEntity.class, com.app.fityo.data_layer.db.SchedeEntity.class, com.app.fityo.data_layer.db.NotificationEntity.class}, version = 5)
@androidx.room.TypeConverters(value = {com.app.fityo.data_layer.db.converters.Converters.class})
public abstract class DbFit extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.app.fityo.data_layer.db.DB.DbFit INSTANCE;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_1_2 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_2_3 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_3_4 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_4_5 = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.app.fityo.data_layer.db.DB.DbFit.Companion Companion = null;
    
    public DbFit() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.app.fityo.data_layer.db.dao.DaoEssercissi essercissiDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.app.fityo.data_layer.db.dao.DaoSchede schedeDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.app.fityo.data_layer.db.dao.DaoNotifications notificationsDao();
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eR\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\bR\u0010\u0010\t\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\bR\u0010\u0010\n\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\bR\u0010\u0010\u000b\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\b\u00a8\u0006\u000f"}, d2 = {"Lcom/app/fityo/data_layer/db/DB/DbFit$Companion;", "", "<init>", "()V", "INSTANCE", "Lcom/app/fityo/data_layer/db/DB/DbFit;", "MIGRATION_1_2", "Landroidx/room/migration/Migration;", "Landroidx/room/migration/Migration;", "MIGRATION_2_3", "MIGRATION_3_4", "MIGRATION_4_5", "getDatabase", "context", "Landroid/content/Context;", "wear_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.app.fityo.data_layer.db.DB.DbFit getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}
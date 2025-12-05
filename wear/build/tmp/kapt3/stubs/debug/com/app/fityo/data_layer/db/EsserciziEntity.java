package com.app.fityo.data_layer.db;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0002\b\"\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001Be\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010#\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\t\u0010$\u001a\u00020\u0005H\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010(\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010)\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010*\u001a\u0004\u0018\u00010\fH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001eJ\t\u0010+\u001a\u00020\u000eH\u00c6\u0003J\t\u0010,\u001a\u00020\u0003H\u00c6\u0003Jz\u0010-\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010.J\u0013\u0010/\u001a\u00020\u000e2\b\u00100\u001a\u0004\u0018\u000101H\u00d6\u0003J\t\u00102\u001a\u00020\u0003H\u00d6\u0001J\t\u00103\u001a\u00020\u0005H\u00d6\u0001R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0019R\u001a\u0010\t\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u001b\u0010\u0013R\u001a\u0010\n\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u001c\u0010\u0013R\u001a\u0010\u000b\u001a\u0004\u0018\u00010\f8\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\r\u001a\u00020\u000e8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0016\u0010\u000f\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0019\u00a8\u00064"}, d2 = {"Lcom/app/fityo/data_layer/db/EsserciziEntity;", "Ljava/io/Serializable;", "id", "", "nome", "", "attrezzo", "nRipetizione", "nSerie", "insometria", "intervallo", "peso", "", "completed", "", "schedaId", "<init>", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;IILjava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Float;ZI)V", "getId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getNome", "()Ljava/lang/String;", "getAttrezzo", "getNRipetizione", "()I", "getNSerie", "getInsometria", "getIntervallo", "getPeso", "()Ljava/lang/Float;", "Ljava/lang/Float;", "getCompleted", "()Z", "getSchedaId", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "copy", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;IILjava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Float;ZI)Lcom/app/fityo/data_layer/db/EsserciziEntity;", "equals", "other", "", "hashCode", "toString", "wear_debug"})
@androidx.room.Entity(tableName = "essercissi", foreignKeys = {@androidx.room.ForeignKey(entity = com.app.fityo.data_layer.db.SchedeEntity.class, parentColumns = {"id"}, childColumns = {"schedaId"}, onDelete = 5)}, indices = {@androidx.room.Index(value = {"schedaId"})})
public final class EsserciziEntity implements java.io.Serializable {
    @androidx.room.PrimaryKey(autoGenerate = true)
    @androidx.room.ColumnInfo(name = "id")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer id = null;
    @androidx.room.ColumnInfo(name = "nome")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String nome = null;
    @androidx.room.ColumnInfo(name = "attrezzo")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String attrezzo = null;
    @androidx.room.ColumnInfo(name = "nRipetizione")
    private final int nRipetizione = 0;
    @androidx.room.ColumnInfo(name = "nSerie")
    private final int nSerie = 0;
    @androidx.room.ColumnInfo(name = "insometria")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer insometria = null;
    @androidx.room.ColumnInfo(name = "intervallo")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer intervallo = null;
    @androidx.room.ColumnInfo(name = "peso")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float peso = null;
    @androidx.room.ColumnInfo(name = "completed")
    private final boolean completed = false;
    @androidx.room.ColumnInfo(name = "schedaId")
    private final int schedaId = 0;
    
    public EsserciziEntity(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, @org.jetbrains.annotations.NotNull()
    java.lang.String nome, @org.jetbrains.annotations.NotNull()
    java.lang.String attrezzo, int nRipetizione, int nSerie, @org.jetbrains.annotations.Nullable()
    java.lang.Integer insometria, @org.jetbrains.annotations.Nullable()
    java.lang.Integer intervallo, @org.jetbrains.annotations.Nullable()
    java.lang.Float peso, boolean completed, int schedaId) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNome() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAttrezzo() {
        return null;
    }
    
    public final int getNRipetizione() {
        return 0;
    }
    
    public final int getNSerie() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getInsometria() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getIntervallo() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getPeso() {
        return null;
    }
    
    public final boolean getCompleted() {
        return false;
    }
    
    public final int getSchedaId() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component1() {
        return null;
    }
    
    public final int component10() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final int component4() {
        return 0;
    }
    
    public final int component5() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component8() {
        return null;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.app.fityo.data_layer.db.EsserciziEntity copy(@org.jetbrains.annotations.Nullable()
    java.lang.Integer id, @org.jetbrains.annotations.NotNull()
    java.lang.String nome, @org.jetbrains.annotations.NotNull()
    java.lang.String attrezzo, int nRipetizione, int nSerie, @org.jetbrains.annotations.Nullable()
    java.lang.Integer insometria, @org.jetbrains.annotations.Nullable()
    java.lang.Integer intervallo, @org.jetbrains.annotations.Nullable()
    java.lang.Float peso, boolean completed, int schedaId) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}
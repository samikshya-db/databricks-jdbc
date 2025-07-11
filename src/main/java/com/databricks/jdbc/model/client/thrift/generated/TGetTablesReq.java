/**
 * Autogenerated by Thrift Compiler (0.19.0)
 *
 * <p>DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package com.databricks.jdbc.model.client.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(
    value = "Autogenerated by Thrift Compiler (0.19.0)",
    date = "2025-05-08")
public class TGetTablesReq
    implements org.apache.thrift.TBase<TGetTablesReq, TGetTablesReq._Fields>,
        java.io.Serializable,
        Cloneable,
        Comparable<TGetTablesReq> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC =
      new org.apache.thrift.protocol.TStruct("TGetTablesReq");

  private static final org.apache.thrift.protocol.TField SESSION_HANDLE_FIELD_DESC =
      new org.apache.thrift.protocol.TField(
          "sessionHandle", org.apache.thrift.protocol.TType.STRUCT, (short) 1);
  private static final org.apache.thrift.protocol.TField CATALOG_NAME_FIELD_DESC =
      new org.apache.thrift.protocol.TField(
          "catalogName", org.apache.thrift.protocol.TType.STRING, (short) 2);
  private static final org.apache.thrift.protocol.TField SCHEMA_NAME_FIELD_DESC =
      new org.apache.thrift.protocol.TField(
          "schemaName", org.apache.thrift.protocol.TType.STRING, (short) 3);
  private static final org.apache.thrift.protocol.TField TABLE_NAME_FIELD_DESC =
      new org.apache.thrift.protocol.TField(
          "tableName", org.apache.thrift.protocol.TType.STRING, (short) 4);
  private static final org.apache.thrift.protocol.TField TABLE_TYPES_FIELD_DESC =
      new org.apache.thrift.protocol.TField(
          "tableTypes", org.apache.thrift.protocol.TType.LIST, (short) 5);
  private static final org.apache.thrift.protocol.TField GET_DIRECT_RESULTS_FIELD_DESC =
      new org.apache.thrift.protocol.TField(
          "getDirectResults", org.apache.thrift.protocol.TType.STRUCT, (short) 1281);
  private static final org.apache.thrift.protocol.TField RUN_ASYNC_FIELD_DESC =
      new org.apache.thrift.protocol.TField(
          "runAsync", org.apache.thrift.protocol.TType.BOOL, (short) 1282);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY =
      new TGetTablesReqStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY =
      new TGetTablesReqTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable TSessionHandle sessionHandle; // required
  public @org.apache.thrift.annotation.Nullable java.lang.String catalogName; // optional
  public @org.apache.thrift.annotation.Nullable java.lang.String schemaName; // optional
  public @org.apache.thrift.annotation.Nullable java.lang.String tableName; // optional
  public @org.apache.thrift.annotation.Nullable java.util.List<java.lang.String>
      tableTypes; // optional
  public @org.apache.thrift.annotation.Nullable TSparkGetDirectResults getDirectResults; // optional
  public boolean runAsync; // optional

  /**
   * The set of fields this struct contains, along with convenience methods for finding and
   * manipulating them.
   */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SESSION_HANDLE((short) 1, "sessionHandle"),
    CATALOG_NAME((short) 2, "catalogName"),
    SCHEMA_NAME((short) 3, "schemaName"),
    TABLE_NAME((short) 4, "tableName"),
    TABLE_TYPES((short) 5, "tableTypes"),
    GET_DIRECT_RESULTS((short) 1281, "getDirectResults"),
    RUN_ASYNC((short) 1282, "runAsync");

    private static final java.util.Map<java.lang.String, _Fields> byName =
        new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /** Find the _Fields constant that matches fieldId, or null if its not found. */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch (fieldId) {
        case 1: // SESSION_HANDLE
          return SESSION_HANDLE;
        case 2: // CATALOG_NAME
          return CATALOG_NAME;
        case 3: // SCHEMA_NAME
          return SCHEMA_NAME;
        case 4: // TABLE_NAME
          return TABLE_NAME;
        case 5: // TABLE_TYPES
          return TABLE_TYPES;
        case 1281: // GET_DIRECT_RESULTS
          return GET_DIRECT_RESULTS;
        case 1282: // RUN_ASYNC
          return RUN_ASYNC;
        default:
          return null;
      }
    }

    /** Find the _Fields constant that matches fieldId, throwing an exception if it is not found. */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null)
        throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /** Find the _Fields constant that matches name, or null if its not found. */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    @Override
    public short getThriftFieldId() {
      return _thriftId;
    }

    @Override
    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __RUNASYNC_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {
    _Fields.CATALOG_NAME,
    _Fields.SCHEMA_NAME,
    _Fields.TABLE_NAME,
    _Fields.TABLE_TYPES,
    _Fields.GET_DIRECT_RESULTS,
    _Fields.RUN_ASYNC
  };
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap =
        new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(
        _Fields.SESSION_HANDLE,
        new org.apache.thrift.meta_data.FieldMetaData(
            "sessionHandle",
            org.apache.thrift.TFieldRequirementType.REQUIRED,
            new org.apache.thrift.meta_data.StructMetaData(
                org.apache.thrift.protocol.TType.STRUCT, TSessionHandle.class)));
    tmpMap.put(
        _Fields.CATALOG_NAME,
        new org.apache.thrift.meta_data.FieldMetaData(
            "catalogName",
            org.apache.thrift.TFieldRequirementType.OPTIONAL,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.STRING, "TPatternOrIdentifier")));
    tmpMap.put(
        _Fields.SCHEMA_NAME,
        new org.apache.thrift.meta_data.FieldMetaData(
            "schemaName",
            org.apache.thrift.TFieldRequirementType.OPTIONAL,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.STRING, "TPatternOrIdentifier")));
    tmpMap.put(
        _Fields.TABLE_NAME,
        new org.apache.thrift.meta_data.FieldMetaData(
            "tableName",
            org.apache.thrift.TFieldRequirementType.OPTIONAL,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.STRING, "TPatternOrIdentifier")));
    tmpMap.put(
        _Fields.TABLE_TYPES,
        new org.apache.thrift.meta_data.FieldMetaData(
            "tableTypes",
            org.apache.thrift.TFieldRequirementType.OPTIONAL,
            new org.apache.thrift.meta_data.ListMetaData(
                org.apache.thrift.protocol.TType.LIST,
                new org.apache.thrift.meta_data.FieldValueMetaData(
                    org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(
        _Fields.GET_DIRECT_RESULTS,
        new org.apache.thrift.meta_data.FieldMetaData(
            "getDirectResults",
            org.apache.thrift.TFieldRequirementType.OPTIONAL,
            new org.apache.thrift.meta_data.StructMetaData(
                org.apache.thrift.protocol.TType.STRUCT, TSparkGetDirectResults.class)));
    tmpMap.put(
        _Fields.RUN_ASYNC,
        new org.apache.thrift.meta_data.FieldMetaData(
            "runAsync",
            org.apache.thrift.TFieldRequirementType.OPTIONAL,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
        TGetTablesReq.class, metaDataMap);
  }

  public TGetTablesReq() {
    this.runAsync = false;
  }

  public TGetTablesReq(TSessionHandle sessionHandle) {
    this();
    this.sessionHandle = sessionHandle;
  }

  /** Performs a deep copy on <i>other</i>. */
  public TGetTablesReq(TGetTablesReq other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetSessionHandle()) {
      this.sessionHandle = new TSessionHandle(other.sessionHandle);
    }
    if (other.isSetCatalogName()) {
      this.catalogName = other.catalogName;
    }
    if (other.isSetSchemaName()) {
      this.schemaName = other.schemaName;
    }
    if (other.isSetTableName()) {
      this.tableName = other.tableName;
    }
    if (other.isSetTableTypes()) {
      java.util.List<java.lang.String> __this__tableTypes =
          new java.util.ArrayList<java.lang.String>(other.tableTypes);
      this.tableTypes = __this__tableTypes;
    }
    if (other.isSetGetDirectResults()) {
      this.getDirectResults = new TSparkGetDirectResults(other.getDirectResults);
    }
    this.runAsync = other.runAsync;
  }

  @Override
  public TGetTablesReq deepCopy() {
    return new TGetTablesReq(this);
  }

  @Override
  public void clear() {
    this.sessionHandle = null;
    this.catalogName = null;
    this.schemaName = null;
    this.tableName = null;
    this.tableTypes = null;
    this.getDirectResults = null;
    this.runAsync = false;
  }

  @org.apache.thrift.annotation.Nullable
  public TSessionHandle getSessionHandle() {
    return this.sessionHandle;
  }

  public TGetTablesReq setSessionHandle(
      @org.apache.thrift.annotation.Nullable TSessionHandle sessionHandle) {
    this.sessionHandle = sessionHandle;
    return this;
  }

  public void unsetSessionHandle() {
    this.sessionHandle = null;
  }

  /** Returns true if field sessionHandle is set (has been assigned a value) and false otherwise */
  public boolean isSetSessionHandle() {
    return this.sessionHandle != null;
  }

  public void setSessionHandleIsSet(boolean value) {
    if (!value) {
      this.sessionHandle = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getCatalogName() {
    return this.catalogName;
  }

  public TGetTablesReq setCatalogName(
      @org.apache.thrift.annotation.Nullable java.lang.String catalogName) {
    this.catalogName = catalogName;
    return this;
  }

  public void unsetCatalogName() {
    this.catalogName = null;
  }

  /** Returns true if field catalogName is set (has been assigned a value) and false otherwise */
  public boolean isSetCatalogName() {
    return this.catalogName != null;
  }

  public void setCatalogNameIsSet(boolean value) {
    if (!value) {
      this.catalogName = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getSchemaName() {
    return this.schemaName;
  }

  public TGetTablesReq setSchemaName(
      @org.apache.thrift.annotation.Nullable java.lang.String schemaName) {
    this.schemaName = schemaName;
    return this;
  }

  public void unsetSchemaName() {
    this.schemaName = null;
  }

  /** Returns true if field schemaName is set (has been assigned a value) and false otherwise */
  public boolean isSetSchemaName() {
    return this.schemaName != null;
  }

  public void setSchemaNameIsSet(boolean value) {
    if (!value) {
      this.schemaName = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getTableName() {
    return this.tableName;
  }

  public TGetTablesReq setTableName(
      @org.apache.thrift.annotation.Nullable java.lang.String tableName) {
    this.tableName = tableName;
    return this;
  }

  public void unsetTableName() {
    this.tableName = null;
  }

  /** Returns true if field tableName is set (has been assigned a value) and false otherwise */
  public boolean isSetTableName() {
    return this.tableName != null;
  }

  public void setTableNameIsSet(boolean value) {
    if (!value) {
      this.tableName = null;
    }
  }

  public int getTableTypesSize() {
    return (this.tableTypes == null) ? 0 : this.tableTypes.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.lang.String> getTableTypesIterator() {
    return (this.tableTypes == null) ? null : this.tableTypes.iterator();
  }

  public void addToTableTypes(java.lang.String elem) {
    if (this.tableTypes == null) {
      this.tableTypes = new java.util.ArrayList<java.lang.String>();
    }
    this.tableTypes.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<java.lang.String> getTableTypes() {
    return this.tableTypes;
  }

  public TGetTablesReq setTableTypes(
      @org.apache.thrift.annotation.Nullable java.util.List<java.lang.String> tableTypes) {
    this.tableTypes = tableTypes;
    return this;
  }

  public void unsetTableTypes() {
    this.tableTypes = null;
  }

  /** Returns true if field tableTypes is set (has been assigned a value) and false otherwise */
  public boolean isSetTableTypes() {
    return this.tableTypes != null;
  }

  public void setTableTypesIsSet(boolean value) {
    if (!value) {
      this.tableTypes = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public TSparkGetDirectResults getGetDirectResults() {
    return this.getDirectResults;
  }

  public TGetTablesReq setGetDirectResults(
      @org.apache.thrift.annotation.Nullable TSparkGetDirectResults getDirectResults) {
    this.getDirectResults = getDirectResults;
    return this;
  }

  public void unsetGetDirectResults() {
    this.getDirectResults = null;
  }

  /**
   * Returns true if field getDirectResults is set (has been assigned a value) and false otherwise
   */
  public boolean isSetGetDirectResults() {
    return this.getDirectResults != null;
  }

  public void setGetDirectResultsIsSet(boolean value) {
    if (!value) {
      this.getDirectResults = null;
    }
  }

  public boolean isRunAsync() {
    return this.runAsync;
  }

  public TGetTablesReq setRunAsync(boolean runAsync) {
    this.runAsync = runAsync;
    setRunAsyncIsSet(true);
    return this;
  }

  public void unsetRunAsync() {
    __isset_bitfield =
        org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __RUNASYNC_ISSET_ID);
  }

  /** Returns true if field runAsync is set (has been assigned a value) and false otherwise */
  public boolean isSetRunAsync() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __RUNASYNC_ISSET_ID);
  }

  public void setRunAsyncIsSet(boolean value) {
    __isset_bitfield =
        org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __RUNASYNC_ISSET_ID, value);
  }

  @Override
  public void setFieldValue(
      _Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
      case SESSION_HANDLE:
        if (value == null) {
          unsetSessionHandle();
        } else {
          setSessionHandle((TSessionHandle) value);
        }
        break;

      case CATALOG_NAME:
        if (value == null) {
          unsetCatalogName();
        } else {
          setCatalogName((java.lang.String) value);
        }
        break;

      case SCHEMA_NAME:
        if (value == null) {
          unsetSchemaName();
        } else {
          setSchemaName((java.lang.String) value);
        }
        break;

      case TABLE_NAME:
        if (value == null) {
          unsetTableName();
        } else {
          setTableName((java.lang.String) value);
        }
        break;

      case TABLE_TYPES:
        if (value == null) {
          unsetTableTypes();
        } else {
          setTableTypes((java.util.List<java.lang.String>) value);
        }
        break;

      case GET_DIRECT_RESULTS:
        if (value == null) {
          unsetGetDirectResults();
        } else {
          setGetDirectResults((TSparkGetDirectResults) value);
        }
        break;

      case RUN_ASYNC:
        if (value == null) {
          unsetRunAsync();
        } else {
          setRunAsync((java.lang.Boolean) value);
        }
        break;
    }
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
      case SESSION_HANDLE:
        return getSessionHandle();

      case CATALOG_NAME:
        return getCatalogName();

      case SCHEMA_NAME:
        return getSchemaName();

      case TABLE_NAME:
        return getTableName();

      case TABLE_TYPES:
        return getTableTypes();

      case GET_DIRECT_RESULTS:
        return getGetDirectResults();

      case RUN_ASYNC:
        return isRunAsync();
    }
    throw new java.lang.IllegalStateException();
  }

  /**
   * Returns true if field corresponding to fieldID is set (has been assigned a value) and false
   * otherwise
   */
  @Override
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
      case SESSION_HANDLE:
        return isSetSessionHandle();
      case CATALOG_NAME:
        return isSetCatalogName();
      case SCHEMA_NAME:
        return isSetSchemaName();
      case TABLE_NAME:
        return isSetTableName();
      case TABLE_TYPES:
        return isSetTableTypes();
      case GET_DIRECT_RESULTS:
        return isSetGetDirectResults();
      case RUN_ASYNC:
        return isSetRunAsync();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof TGetTablesReq) return this.equals((TGetTablesReq) that);
    return false;
  }

  public boolean equals(TGetTablesReq that) {
    if (that == null) return false;
    if (this == that) return true;

    boolean this_present_sessionHandle = true && this.isSetSessionHandle();
    boolean that_present_sessionHandle = true && that.isSetSessionHandle();
    if (this_present_sessionHandle || that_present_sessionHandle) {
      if (!(this_present_sessionHandle && that_present_sessionHandle)) return false;
      if (!this.sessionHandle.equals(that.sessionHandle)) return false;
    }

    boolean this_present_catalogName = true && this.isSetCatalogName();
    boolean that_present_catalogName = true && that.isSetCatalogName();
    if (this_present_catalogName || that_present_catalogName) {
      if (!(this_present_catalogName && that_present_catalogName)) return false;
      if (!this.catalogName.equals(that.catalogName)) return false;
    }

    boolean this_present_schemaName = true && this.isSetSchemaName();
    boolean that_present_schemaName = true && that.isSetSchemaName();
    if (this_present_schemaName || that_present_schemaName) {
      if (!(this_present_schemaName && that_present_schemaName)) return false;
      if (!this.schemaName.equals(that.schemaName)) return false;
    }

    boolean this_present_tableName = true && this.isSetTableName();
    boolean that_present_tableName = true && that.isSetTableName();
    if (this_present_tableName || that_present_tableName) {
      if (!(this_present_tableName && that_present_tableName)) return false;
      if (!this.tableName.equals(that.tableName)) return false;
    }

    boolean this_present_tableTypes = true && this.isSetTableTypes();
    boolean that_present_tableTypes = true && that.isSetTableTypes();
    if (this_present_tableTypes || that_present_tableTypes) {
      if (!(this_present_tableTypes && that_present_tableTypes)) return false;
      if (!this.tableTypes.equals(that.tableTypes)) return false;
    }

    boolean this_present_getDirectResults = true && this.isSetGetDirectResults();
    boolean that_present_getDirectResults = true && that.isSetGetDirectResults();
    if (this_present_getDirectResults || that_present_getDirectResults) {
      if (!(this_present_getDirectResults && that_present_getDirectResults)) return false;
      if (!this.getDirectResults.equals(that.getDirectResults)) return false;
    }

    boolean this_present_runAsync = true && this.isSetRunAsync();
    boolean that_present_runAsync = true && that.isSetRunAsync();
    if (this_present_runAsync || that_present_runAsync) {
      if (!(this_present_runAsync && that_present_runAsync)) return false;
      if (this.runAsync != that.runAsync) return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetSessionHandle()) ? 131071 : 524287);
    if (isSetSessionHandle()) hashCode = hashCode * 8191 + sessionHandle.hashCode();

    hashCode = hashCode * 8191 + ((isSetCatalogName()) ? 131071 : 524287);
    if (isSetCatalogName()) hashCode = hashCode * 8191 + catalogName.hashCode();

    hashCode = hashCode * 8191 + ((isSetSchemaName()) ? 131071 : 524287);
    if (isSetSchemaName()) hashCode = hashCode * 8191 + schemaName.hashCode();

    hashCode = hashCode * 8191 + ((isSetTableName()) ? 131071 : 524287);
    if (isSetTableName()) hashCode = hashCode * 8191 + tableName.hashCode();

    hashCode = hashCode * 8191 + ((isSetTableTypes()) ? 131071 : 524287);
    if (isSetTableTypes()) hashCode = hashCode * 8191 + tableTypes.hashCode();

    hashCode = hashCode * 8191 + ((isSetGetDirectResults()) ? 131071 : 524287);
    if (isSetGetDirectResults()) hashCode = hashCode * 8191 + getDirectResults.hashCode();

    hashCode = hashCode * 8191 + ((isSetRunAsync()) ? 131071 : 524287);
    if (isSetRunAsync()) hashCode = hashCode * 8191 + ((runAsync) ? 131071 : 524287);

    return hashCode;
  }

  @Override
  public int compareTo(TGetTablesReq other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetSessionHandle(), other.isSetSessionHandle());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSessionHandle()) {
      lastComparison =
          org.apache.thrift.TBaseHelper.compareTo(this.sessionHandle, other.sessionHandle);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetCatalogName(), other.isSetCatalogName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCatalogName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.catalogName, other.catalogName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetSchemaName(), other.isSetSchemaName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSchemaName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.schemaName, other.schemaName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetTableName(), other.isSetTableName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTableName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tableName, other.tableName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetTableTypes(), other.isSetTableTypes());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTableTypes()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tableTypes, other.tableTypes);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison =
        java.lang.Boolean.compare(isSetGetDirectResults(), other.isSetGetDirectResults());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetGetDirectResults()) {
      lastComparison =
          org.apache.thrift.TBaseHelper.compareTo(this.getDirectResults, other.getDirectResults);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetRunAsync(), other.isSetRunAsync());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRunAsync()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.runAsync, other.runAsync);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  @Override
  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  @Override
  public void write(org.apache.thrift.protocol.TProtocol oprot)
      throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("TGetTablesReq(");
    boolean first = true;

    sb.append("sessionHandle:");
    if (this.sessionHandle == null) {
      sb.append("null");
    } else {
      sb.append(this.sessionHandle);
    }
    first = false;
    if (isSetCatalogName()) {
      if (!first) sb.append(", ");
      sb.append("catalogName:");
      if (this.catalogName == null) {
        sb.append("null");
      } else {
        sb.append(this.catalogName);
      }
      first = false;
    }
    if (isSetSchemaName()) {
      if (!first) sb.append(", ");
      sb.append("schemaName:");
      if (this.schemaName == null) {
        sb.append("null");
      } else {
        sb.append(this.schemaName);
      }
      first = false;
    }
    if (isSetTableName()) {
      if (!first) sb.append(", ");
      sb.append("tableName:");
      if (this.tableName == null) {
        sb.append("null");
      } else {
        sb.append(this.tableName);
      }
      first = false;
    }
    if (isSetTableTypes()) {
      if (!first) sb.append(", ");
      sb.append("tableTypes:");
      if (this.tableTypes == null) {
        sb.append("null");
      } else {
        sb.append(this.tableTypes);
      }
      first = false;
    }
    if (isSetGetDirectResults()) {
      if (!first) sb.append(", ");
      sb.append("getDirectResults:");
      if (this.getDirectResults == null) {
        sb.append("null");
      } else {
        sb.append(this.getDirectResults);
      }
      first = false;
    }
    if (isSetRunAsync()) {
      if (!first) sb.append(", ");
      sb.append("runAsync:");
      sb.append(this.runAsync);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (sessionHandle == null) {
      throw new org.apache.thrift.protocol.TProtocolException(
          "Required field 'sessionHandle' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (sessionHandle != null) {
      sessionHandle.validate();
    }
    if (getDirectResults != null) {
      getDirectResults.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(
          new org.apache.thrift.protocol.TCompactProtocol(
              new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in)
      throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and
      // doesn't call the default constructor.
      __isset_bitfield = 0;
      read(
          new org.apache.thrift.protocol.TCompactProtocol(
              new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TGetTablesReqStandardSchemeFactory
      implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public TGetTablesReqStandardScheme getScheme() {
      return new TGetTablesReqStandardScheme();
    }
  }

  private static class TGetTablesReqStandardScheme
      extends org.apache.thrift.scheme.StandardScheme<TGetTablesReq> {

    @Override
    public void read(org.apache.thrift.protocol.TProtocol iprot, TGetTablesReq struct)
        throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true) {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
          break;
        }
        switch (schemeField.id) {
          case 1: // SESSION_HANDLE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.sessionHandle = new TSessionHandle();
              struct.sessionHandle.read(iprot);
              struct.setSessionHandleIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // CATALOG_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.catalogName = iprot.readString();
              struct.setCatalogNameIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SCHEMA_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.schemaName = iprot.readString();
              struct.setSchemaNameIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TABLE_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.tableName = iprot.readString();
              struct.setTableNameIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // TABLE_TYPES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list248 = iprot.readListBegin();
                struct.tableTypes = new java.util.ArrayList<java.lang.String>(_list248.size);
                @org.apache.thrift.annotation.Nullable java.lang.String _elem249;
                for (int _i250 = 0; _i250 < _list248.size; ++_i250) {
                  _elem249 = iprot.readString();
                  struct.tableTypes.add(_elem249);
                }
                iprot.readListEnd();
              }
              struct.setTableTypesIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 1281: // GET_DIRECT_RESULTS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.getDirectResults = new TSparkGetDirectResults();
              struct.getDirectResults.read(iprot);
              struct.setGetDirectResultsIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 1282: // RUN_ASYNC
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.runAsync = iprot.readBool();
              struct.setRunAsyncIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    @Override
    public void write(org.apache.thrift.protocol.TProtocol oprot, TGetTablesReq struct)
        throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.sessionHandle != null) {
        oprot.writeFieldBegin(SESSION_HANDLE_FIELD_DESC);
        struct.sessionHandle.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.catalogName != null) {
        if (struct.isSetCatalogName()) {
          oprot.writeFieldBegin(CATALOG_NAME_FIELD_DESC);
          oprot.writeString(struct.catalogName);
          oprot.writeFieldEnd();
        }
      }
      if (struct.schemaName != null) {
        if (struct.isSetSchemaName()) {
          oprot.writeFieldBegin(SCHEMA_NAME_FIELD_DESC);
          oprot.writeString(struct.schemaName);
          oprot.writeFieldEnd();
        }
      }
      if (struct.tableName != null) {
        if (struct.isSetTableName()) {
          oprot.writeFieldBegin(TABLE_NAME_FIELD_DESC);
          oprot.writeString(struct.tableName);
          oprot.writeFieldEnd();
        }
      }
      if (struct.tableTypes != null) {
        if (struct.isSetTableTypes()) {
          oprot.writeFieldBegin(TABLE_TYPES_FIELD_DESC);
          {
            oprot.writeListBegin(
                new org.apache.thrift.protocol.TList(
                    org.apache.thrift.protocol.TType.STRING, struct.tableTypes.size()));
            for (java.lang.String _iter251 : struct.tableTypes) {
              oprot.writeString(_iter251);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.getDirectResults != null) {
        if (struct.isSetGetDirectResults()) {
          oprot.writeFieldBegin(GET_DIRECT_RESULTS_FIELD_DESC);
          struct.getDirectResults.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetRunAsync()) {
        oprot.writeFieldBegin(RUN_ASYNC_FIELD_DESC);
        oprot.writeBool(struct.runAsync);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }
  }

  private static class TGetTablesReqTupleSchemeFactory
      implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public TGetTablesReqTupleScheme getScheme() {
      return new TGetTablesReqTupleScheme();
    }
  }

  private static class TGetTablesReqTupleScheme
      extends org.apache.thrift.scheme.TupleScheme<TGetTablesReq> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TGetTablesReq struct)
        throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot =
          (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.sessionHandle.write(oprot);
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetCatalogName()) {
        optionals.set(0);
      }
      if (struct.isSetSchemaName()) {
        optionals.set(1);
      }
      if (struct.isSetTableName()) {
        optionals.set(2);
      }
      if (struct.isSetTableTypes()) {
        optionals.set(3);
      }
      if (struct.isSetGetDirectResults()) {
        optionals.set(4);
      }
      if (struct.isSetRunAsync()) {
        optionals.set(5);
      }
      oprot.writeBitSet(optionals, 6);
      if (struct.isSetCatalogName()) {
        oprot.writeString(struct.catalogName);
      }
      if (struct.isSetSchemaName()) {
        oprot.writeString(struct.schemaName);
      }
      if (struct.isSetTableName()) {
        oprot.writeString(struct.tableName);
      }
      if (struct.isSetTableTypes()) {
        {
          oprot.writeI32(struct.tableTypes.size());
          for (java.lang.String _iter252 : struct.tableTypes) {
            oprot.writeString(_iter252);
          }
        }
      }
      if (struct.isSetGetDirectResults()) {
        struct.getDirectResults.write(oprot);
      }
      if (struct.isSetRunAsync()) {
        oprot.writeBool(struct.runAsync);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TGetTablesReq struct)
        throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot =
          (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.sessionHandle = new TSessionHandle();
      struct.sessionHandle.read(iprot);
      struct.setSessionHandleIsSet(true);
      java.util.BitSet incoming = iprot.readBitSet(6);
      if (incoming.get(0)) {
        struct.catalogName = iprot.readString();
        struct.setCatalogNameIsSet(true);
      }
      if (incoming.get(1)) {
        struct.schemaName = iprot.readString();
        struct.setSchemaNameIsSet(true);
      }
      if (incoming.get(2)) {
        struct.tableName = iprot.readString();
        struct.setTableNameIsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TList _list253 =
              iprot.readListBegin(org.apache.thrift.protocol.TType.STRING);
          struct.tableTypes = new java.util.ArrayList<java.lang.String>(_list253.size);
          @org.apache.thrift.annotation.Nullable java.lang.String _elem254;
          for (int _i255 = 0; _i255 < _list253.size; ++_i255) {
            _elem254 = iprot.readString();
            struct.tableTypes.add(_elem254);
          }
        }
        struct.setTableTypesIsSet(true);
      }
      if (incoming.get(4)) {
        struct.getDirectResults = new TSparkGetDirectResults();
        struct.getDirectResults.read(iprot);
        struct.setGetDirectResultsIsSet(true);
      }
      if (incoming.get(5)) {
        struct.runAsync = iprot.readBool();
        struct.setRunAsyncIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(
      org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme())
            ? STANDARD_SCHEME_FACTORY
            : TUPLE_SCHEME_FACTORY)
        .getScheme();
  }
}

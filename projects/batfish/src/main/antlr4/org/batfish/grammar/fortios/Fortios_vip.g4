parser grammar Fortios_address;

options {
  tokenVocab = FortiosLexer;
}

cf_vip: VIP newline cfv_edit*;

cfv_edit: EDIT vip_name = str newline cfve* NEXT newline;

cfve: cfv_set; // | cfve_config;

cfve_config: CONFIG cfvec_tagging;

cfvec_tagging: REALSERVERS newline cfvect_edit* END newline;

cfvect_edit: EDIT str newline cfvect* NEXT newline;

cfvect: SET cfvect_set_ip_or_port;

cfvect_set_ip_or_port: cfvect_set_ip | cfvect_set_port;

cfvect_set_ip: IP ip = ip_address newline;
cfvect_set_port: PORT  port = some_port newline;

cfv_set: SET cfv_set_singletons;

cfv_set_singletons:
    cfv_set_extintf
    | cfv_set_extip
    | cfv_set_extport
    | cfv_set_mappedip
    | cfv_set_mappedport
    | cfv_set_portforward
    | cfv_set_uuid
    | cfv_set_null

;

cfv_set_extintf: EXTINTF name = double_quoted_string newline;
cfv_set_extip: EXTIP ip = ip_address newline;
cfv_set_extport: EXTPORT port = some_port newline;
cfv_set_mappedip: MAPPEDIP ip = double_quoted_string newline;
cfv_set_mappedport: MAPPEDPORT port = some_port newline;
cfv_set_portforward: PORTFORWARD value = enable_or_disable newline;
cfv_set_uuid: UUID id = str newline;
cfv_set_null:
    (
        HTTP_MULTIPLEX
        | LDB_METHOD
        | MONITOR
        | TYPE
        | SERVER_TYPE
    ) null_rest_of_line;
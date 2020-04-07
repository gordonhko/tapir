The Access Policy Rule Engine

// PRP

1) Append-only
- No race condition
2) Multi-tenant
3) In-memory snapshot from database (RowBase)
- tenant-based 
- data size is limited
- most requests are reading
- table join and other operations implemented by application


// PAP

1) Support system variable as left-operand such as $TIME, $TODAY, $OS
2) Support 3rd party evaluation, 
- operator = “@(custom_function)”.
- no operands
- passing userId, tenantID/companyID, objectType, objectID to @(custom_function) 


// PIP
1) pseudo metadata and biz data retrieve


// PEP
1) Support both command line and restful


// PDP

1) “AND” operators applying to all rules in the same policy
2) “OR” operators applying to all policies

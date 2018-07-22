/*

  silcversion.h 

  Author: Pekka Riikonen <priikone@silcnet.org>

  Copyright (C) 1997 - 2002 Pekka Riikonen

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; version 2 of the License.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

*/

#ifndef SILCVERSION_H
#define SILCVERSION_H

#ifdef __cplusplus
extern "C" {
#endif

#include "version_internal.h"

/* SILC Protocol version number */
#define SILC_PROTOCOL_VERSION_CURRENT 12

/* SILC version string */
#define silc_version SILC_VERSION_STRING
#define silc_dist_version SILC_DIST_VERSION_STRING
#define silc_version_string SILC_PROTOCOL_VERSION_STRING
#define silc_name SILC_NAME
#define silc_fullname "Secure Internet Live Conferencing"

#ifdef __cplusplus
}
#endif

#endif /* SILCVERSION_H */

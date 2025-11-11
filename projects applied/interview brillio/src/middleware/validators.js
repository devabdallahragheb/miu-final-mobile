const { body, param, query } = require('express-validator');



const userValidators = {
  create: [
    body('name')
      .trim()
      .notEmpty().withMessage('Name is required')
,    
    body('email')
      .trim()
      .notEmpty().withMessage('Email is required')
     ,
    body('age')
      .optional()
    ,
  ],

  update: [
    body('name')
      .optional()
     ,
    body('email')
      .optional()
 ,
    body('age')
      .optional()
     ,
  ],

  idParam: [
    param('id')
      .isMongoId().withMessage('Invalid user ID format')
  ],
  listQuery: [
    query('page')
      .optional()
,    
    query('limit')
      .optional()

,  ]
};

module.exports = {
  userValidators
};
